package com.example.guru2_contestapp

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var myContestRecycler : RecyclerView       // 내 공모전 리스트
    lateinit var recomTeamtRecycler : RecyclerView      // 추천 공모전 팀 리스트
    lateinit var recomContestRecycler : RecyclerView   // 추천 공모전 리스트


    lateinit var userProfile : ImageView
    lateinit var userName : TextView
    lateinit var name : String
    lateinit var joinTeam : TextView
    lateinit var applicantTeam : TextView
    lateinit var nonMyTeam : TextView

    lateinit var myTeamList : ArrayList<WishItem>      // 내 공모전 (만든 팀 & 신청한 팀)

    lateinit var recomConList : ArrayList<WishItem>    // 추천 공모전 리스트
    lateinit var recomTeamList : ArrayList<WishItem>   // 추천 팀 리스트

    lateinit var allConList : ArrayList<WishItem>     // 모든 추천 외의 공모전 리스트
    lateinit var allTeamList : ArrayList<WishItem>    // 모든 추천 외의 팀 리스트

    lateinit var swipeRefreshLayout: SwipeRefreshLayout   // 새로고침에 필요

    var c_num : Int = -1
    lateinit var c_name : String
    lateinit var c_end : String
    lateinit var c_photo : String
    var t_num : Int = -1
    lateinit var t_name : String
    lateinit var t_end_date : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false)
        var context: Context = requireContext()

        // 키로 저장해둔 사용자 id 불러오기
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        // 배열 초기화
        myTeamList = ArrayList()
        recomConList = ArrayList()
        recomTeamList = ArrayList()
        allConList = ArrayList()
        allTeamList = ArrayList()

        userProfile = view.findViewById(R.id.JuserProfile)
        userName = view.findViewById(R.id.JuserName2)
        joinTeam = view.findViewById(R.id.JjoinTeam)
        applicantTeam = view.findViewById(R.id.JapplicantTeam)
        nonMyTeam = view.findViewById(R.id.JnonMyTeam)

        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        try {
            var cursor: Cursor
            cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '${USER_ID}';", null)
            cursor.moveToFirst()

            if (cursor.getCount() != 1) {
                Toast.makeText(activity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                var m_profile = cursor.getString(cursor.getColumnIndex("m_profile"))
                var m_name = cursor.getString(cursor.getColumnIndex("m_name"))
                var m_interest = cursor.getString(cursor.getColumnIndex("m_interest"))

                if (m_profile == null || m_profile == ""){
                    m_profile = "profile0"
                }

                var profile_src = this.resources.getIdentifier(m_profile,"drawable", "com.example.guru2_contestapp")
                userProfile.setImageResource(profile_src)


                name = "안녕하세요 " + m_name + " 님"
                userName.text = name

                // user가 참여한 공모전 (경력)
                var join_cursor: Cursor
                join_cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '${USER_ID}' AND state = 5;", null )
                joinTeam.text = join_cursor.getCount().toString() + " 개"

                // user가 지원한 공모전
                var appli_cursor: Cursor
                appli_cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '${USER_ID}' AND state = 0 OR state = 1 OR state = -1;", null)
                applicantTeam.text = appli_cursor.getCount().toString() + " 개"


                // user의 내 팀 리스트 (호스트 & 신청 & 팀 완성 전 수락 상태)
                var myTeam_cursor: Cursor
                myTeam_cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '${USER_ID}' AND state = 2;", null)
                if (myTeam_cursor.getCount() > 0){
                    while(myTeam_cursor.moveToNext()){
                        t_num = myTeam_cursor.getInt(myTeam_cursor.getColumnIndex("t_num"))

                        var myTeamInfo_cursor: Cursor
                        myTeamInfo_cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = ${t_num};", null)
                        myTeamInfo_cursor.moveToFirst()

                        if (myTeamInfo_cursor.getCount() == 1) {
                            t_end_date = myTeamInfo_cursor.getString(myTeamInfo_cursor.getColumnIndex("t_end_date"))

                            var conDeadline = checkDays(t_end_date)

                            if (conDeadline < 0) {
                                // 모집 기간이 종료된 경우 리스트에 추가 x

                            } else {
                                c_num = myTeamInfo_cursor.getInt(myTeamInfo_cursor.getColumnIndex("c_num"))
                                var con_cursor: Cursor
                                con_cursor = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = ${c_num};", null)

                                if (con_cursor.getCount() == 1) {
                                    con_cursor.moveToNext()
                                    t_name = myTeamInfo_cursor.getString(myTeamInfo_cursor.getColumnIndex("t_name"))
                                    var c_photo = con_cursor.getString(con_cursor.getColumnIndex("c_photo"))
                                    Log.d("c_photo",c_photo)
                                    myTeamList.add(WishItem(t_num, "팀장입니다", c_photo, t_name))
                                    Log.d("==== my team ===", t_num.toString())
                                }
                            }
                        }
                    }
                }

                myTeam_cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '${USER_ID}' AND state = 0 OR state = 1;", null)

                if (myTeam_cursor.getCount() != 0){
                    while(myTeam_cursor.moveToNext()){
                        t_num = myTeam_cursor.getInt(myTeam_cursor.getColumnIndex("t_num"))

                        var myTeamInfo_cursor: Cursor
                        myTeamInfo_cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = ${t_num};", null)
                        myTeamInfo_cursor.moveToFirst()

                        if (myTeamInfo_cursor.getCount() == 1) {
                            t_end_date = myTeamInfo_cursor.getString(myTeamInfo_cursor.getColumnIndex("t_end_date"))

                            var conDeadline = checkDays(t_end_date)

                            if (conDeadline < 0) {
                                // 모집 기간이 종료된 경우 리스트에 추가 x

                            } else {
                                var state = ""
                                if (myTeam_cursor.getInt(myTeam_cursor.getColumnIndex("state")) == 0){
                                    state = "대기 중"
                                } else {
                                    state = "팀원 수락"
                                }

                                c_num = myTeamInfo_cursor.getInt(myTeamInfo_cursor.getColumnIndex("c_num"))
                                var con_cursor: Cursor
                                con_cursor = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = ${c_num};", null)

                                if (con_cursor.getCount() == 1) {
                                    con_cursor.moveToNext()
                                    t_name = myTeamInfo_cursor.getString(myTeamInfo_cursor.getColumnIndex("t_name"))
                                    var c_photo = con_cursor.getString(con_cursor.getColumnIndex("c_photo"))
                                    myTeamList.add(WishItem(t_num, state, c_photo, t_name))
                                    Log.d("==== my team ===", t_num.toString())
                                }
                            }
                        }
                    }
                }

                if (myTeamList.size > 0){
                    nonMyTeam.visibility = GONE
                    myContestRecycler = view.findViewById(R.id.JmyContestRecycler)
                    myContestRecycler.setHasFixedSize(true)
                    myContestRecycler.adapter = WishListAdapter(myTeamList)
                } else {
                    nonMyTeam.visibility = VISIBLE
                }


                // user가 관심있어 하는 공모전
                var recomContest_cursor: Cursor
                recomContest_cursor = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_section = '${m_interest}' ORDER BY random();", null)

                // user가 관심있어 하는 공모전 팀
                lateinit var recomTeam_cursor: Cursor

                if (recomContest_cursor.getCount() > 0) {
                    while (recomContest_cursor.moveToNext()) {
                        c_end = recomContest_cursor.getString(recomContest_cursor.getColumnIndex("c_end"))

                        var conDeadline = checkDays(c_end)

                        if (conDeadline < 0) {
                            // 모집 기간이 종료된 경우 리스트에 추가 x

                        } else {
                            // 모집 기간 내에 있는 추천 공모전
                            c_num = recomContest_cursor.getInt(recomContest_cursor.getColumnIndex("c_num"))
                            c_name = recomContest_cursor.getString(recomContest_cursor.getColumnIndex("c_name"))
                            c_photo = recomContest_cursor.getString(recomContest_cursor.getColumnIndex("c_photo"))

                            var conDeadlineText = "모집 " + conDeadline.toString() + "일 전"

                            // user가 관심있어 하는 공모전 중 신청 가능한 팀
                            recomTeam_cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE c_num = ${c_num} ORDER BY random();", null)

                            while (recomTeam_cursor.moveToNext()) {
                                t_end_date = recomTeam_cursor.getString(recomTeam_cursor.getColumnIndex("t_end_date"))

                                var teamDeadline = checkDays(t_end_date)

                                if (teamDeadline < 0) {
                                    // 모집 기간이 종료된 경우 리스트에 추가 x

                                } else {
                                    // 모집 기간 내에 있는 추천 공모전 팀
                                    t_num = recomTeam_cursor.getInt(recomTeam_cursor.getColumnIndex("t_num"))
                                    t_name = recomTeam_cursor.getString(recomTeam_cursor.getColumnIndex("t_name"))


                                    var teamDeadlineText = "모집 " + teamDeadline.toString() + "일 전"

                                    // 추천 팀 리스트에 추가
                                    allTeamList.add(WishItem(t_num, teamDeadlineText, c_photo, t_name))
                                }
                            }

                            // 추천 공모전 리스트에 추가
                            allConList.add(WishItem(c_num, conDeadlineText, c_photo, c_name))
                        }
                    }
                }

                // usr의 관심 외의 공모전 (추천 공모전이 5개 미만일 때 필요)
                var restContest_cursor : Cursor
                restContest_cursor = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_section != '${m_interest}' ORDER BY random();", null)

                // user가 관심있어 하는 공모전외의 공모전 중 신청 가능한 팀
                lateinit var restTeam_cursor: Cursor

                if (restContest_cursor.getCount() > 0){
                    while(restContest_cursor.moveToNext()) {
                        c_end = restContest_cursor.getString(restContest_cursor.getColumnIndex("c_end"))

                        var conDeadline = checkDays(c_end)

                        if (conDeadline < 0) {
                            // 모집 기간이 종료된 경우 리스트에 추가 x

                        } else {
                            // 모집 기간 내에 있는 추천 공모전
                            c_num = restContest_cursor.getInt(restContest_cursor.getColumnIndex("c_num"))
                            c_name = restContest_cursor.getString(restContest_cursor.getColumnIndex("c_name"))
                            c_photo = restContest_cursor.getString(restContest_cursor.getColumnIndex("c_photo"))

                            var conDeadlineText = "모집 " + conDeadline.toString() + "일 전"

                            // user가 관심있어 하는 공모전외의 공모전 중 신청 가능한 팀
                            restTeam_cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE c_num = ${c_num} ORDER BY random();", null)

                            while (restTeam_cursor.moveToNext()) {
                                t_end_date = restTeam_cursor.getString(restTeam_cursor.getColumnIndex("t_end_date"))

                                var teamDeadline = checkDays(t_end_date)

                                if (teamDeadline < 0) {
                                    // 모집 기간이 종료된 경우 리스트에 추가 x

                                } else {
                                    // 모집 기간 내에 있는 추천 외의 공모전 팀
                                    t_num = restTeam_cursor.getInt(restTeam_cursor.getColumnIndex("t_num"))
                                    t_name = restTeam_cursor.getString(restTeam_cursor.getColumnIndex("t_name"))

                                    var teamDeadlineText = "모집 " + teamDeadline.toString() + "일 전"

                                    // 추천 외의 남은 팀 리스트에 추가
                                    allTeamList.add(WishItem(t_num, teamDeadlineText, c_photo, t_name))
                                }
                            }

                            // 추천 외의 남은 공모전 리스트에 추가
                            allConList.add(WishItem(c_num, conDeadlineText, c_photo, c_name))
                        }
                    }
                }


                var conNum = allConList.size
                var teamNum = allTeamList.size
                for (i in 0..4){
                    if (i < conNum){
                        recomConList.add(allConList[i])
                    }

                    if (i < teamNum){
                        recomTeamList.add(allTeamList[i])
                    }
                }

                recomTeamtRecycler = view.findViewById(R.id.JrecomTeamtRecycler)
                recomTeamtRecycler.setHasFixedSize(true)
                recomTeamtRecycler.adapter = WishListAdapter(recomTeamList)

                recomContestRecycler = view.findViewById(R.id.JrecomContestRecycler)
                recomContestRecycler.setHasFixedSize(true)
                recomContestRecycler.adapter = WishListAdapter(recomConList)
            }
            } catch (e: Exception){
                Log.e("Error", e.message.toString())
            } finally {
                sqlitedb.close()
            }

        // 당겨서 새로고침
        swipeRefreshLayout=view.findViewById(R.id.JswipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            val WishListAdapter= activity?.let { WishListAdapter(myTeamList) }
            if (WishListAdapter != null) {
                WishListAdapter.notifyDataSetChanged()
            }

            myContestRecycler = view.findViewById(R.id.JmyContestRecycler)
            myContestRecycler.setHasFixedSize(true)
            myContestRecycler.adapter = WishListAdapter(myTeamList)

            recomTeamtRecycler = view.findViewById(R.id.JrecomTeamtRecycler)
            recomTeamtRecycler.setHasFixedSize(true)
            recomTeamtRecycler.adapter = WishListAdapter(recomTeamList)

            recomContestRecycler = view.findViewById(R.id.JrecomContestRecycler)
            recomContestRecycler.setHasFixedSize(true)
            recomContestRecycler.adapter = WishListAdapter(recomConList)

            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
            swipeRefreshLayout.isRefreshing=false
        }

        return view
    }

    // 날짜 차이 구하기
    private fun checkDays(t_end_date: String): Int {

        var Eday_arr = t_end_date.split(".")
        val deadline = Calendar.getInstance().apply {
            set(Calendar.YEAR, Eday_arr[0].toInt())
            set(Calendar.MONTH, Eday_arr[1].toInt() - 1)
            set(Calendar.DAY_OF_MONTH, Eday_arr[2].toInt())
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        var today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val calcDate = (deadline - today) / (24 * 60 * 60 * 1000)
        return calcDate.toInt()
    }
}