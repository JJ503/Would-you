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
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class BuildTeamListFragment : Fragment() {


    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var t_name: String
    lateinit var c_name: String
    lateinit var t_end_date: String
    lateinit var t_need_part: String
    lateinit var c_photo : String
    var photo_src= -1// 사진 경로

    var t_now_num: Int = -1
    var t_total_num: Int = -1
    var c_num: Int = -1

    //lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var  rv_applyTeam: RecyclerView

    // 리사이클러뷰 어댑터에 넘겨줄 리스트로, 만든 팀 목록이 담김
    var buildTeamList: ArrayList<TeamItem> = ArrayList<TeamItem>()

    var build_t_now_num : ArrayList<Int> =  ArrayList<Int>()
    var check_t_now_num : ArrayList<Int> =  ArrayList<Int>() //새로고침을 위한 변수

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        var v_buildTeamList = inflater.inflate(R.layout.fragment_team_list, container, false)

        buildTeamList = ArrayList()
        build_t_now_num =ArrayList()

        // 현재 로그인 중인 사용자 정보 가져오기
        var context: Context = requireContext()
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase


        // 리사이클러뷰에 적용시킬 만든 팀 정보를 DB에서 가져와 리스트에 저장한다.
        try {
            if (sqlitedb != null) {
                lateinit var cursor1: Cursor
                cursor1 = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 2;", null)


                lateinit var cursor2: Cursor
                var t_num: Int = -1

                lateinit var cursor3: Cursor
                if (cursor1.getCount() != 0) {
                    while (cursor1.moveToNext()) {
                        t_num = cursor1.getInt(cursor1.getColumnIndex("t_num"))
                        cursor2 = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = " + t_num + ";", null)

                        if (cursor2.moveToNext()) {
                            c_num = cursor2.getInt(cursor2.getColumnIndex("c_num"))
                            cursor3 = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = " + c_num + ";", null)

                            if (cursor3.moveToNext())
                            {
                                c_photo =  cursor3.getString(cursor3.getColumnIndex("c_photo"))
                                c_name = cursor3.getString(cursor3.getColumnIndex("c_name"))
                            }

                            t_name = cursor2.getString(cursor2.getColumnIndex("t_name")).toString()
                            t_now_num = cursor2.getInt(cursor2.getColumnIndex("t_now_num"))
                            t_total_num = cursor2.getInt(cursor2.getColumnIndex("t_total_num"))
                            t_end_date = cursor2.getString(cursor2.getColumnIndex("t_end_date"))
                            t_need_part = cursor2.getString(cursor2.getColumnIndex("t_need_part"))
                        }

                        photo_src =  this.resources.getIdentifier(c_photo,"drawable", "com.example.guru2_contestapp")
                        buildTeamList.add(
                                TeamItem(t_num, photo_src, t_name, c_name,
                                        t_now_num, t_total_num, t_end_date, t_need_part)
                        )
                       build_t_now_num.add(t_now_num)
                    }
                }
                cursor1.close()
                cursor2.close()
                cursor3.close()
            }
        }catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }



        rv_applyTeam = v_buildTeamList.findViewById<RecyclerView>(R.id.rv_team)

        rv_applyTeam.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_applyTeam.setHasFixedSize(true)
        rv_applyTeam.adapter = BuildTeamListAdapter(buildTeamList)


/*
        // 당겨서 새로고침
        swipeRefreshLayout=v_buildTeamList.findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            rv_applyTeam = v_buildTeamList.findViewById<RecyclerView>(R.id.rv_team)

            rv_applyTeam.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv_applyTeam.setHasFixedSize(true)
            rv_applyTeam.adapter = BuildTeamListAdapter(buildTeamList)

            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
            swipeRefreshLayout.isRefreshing=false
        }

*/


        return v_buildTeamList
    }
/*
    // 직업 정보 혹은 프로필이 변경된 경우 새로고침되도록 한다.
    override fun onResume() {
        super.onResume()

        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        lateinit var cursor1: Cursor
        lateinit var cursor2: Cursor
        var t_num: Int = -1

        check_t_now_num =ArrayList()
            try {
                if (sqlitedb != null) {
                    cursor1 = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 2;", null)

                    if (cursor1.getCount() != 0) {
                        while (cursor1.moveToNext()) {
                            t_num = cursor1.getInt(cursor1.getColumnIndex("t_num"))
                            cursor2 = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = " + t_num + ";", null)

                            if (cursor2.moveToNext()) {
                                t_now_num = cursor2.getInt(cursor2.getColumnIndex("t_now_num"))
                            }

                            if( build_t_now_num.isEmpty() ==true) {
                                check_t_now_num.add(t_now_num)
                                build_t_now_num.add(t_now_num)
                            }else{
                                check_t_now_num.add(t_now_num)
                             }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            } finally {
                cursor1.close()
                cursor2.close()
            }
        sqlitedb.close()
        dbManager.close()

        Log.d("~~build~~~~",build_t_now_num.size.toString())
        Log.d("~~ch~~~~~",check_t_now_num.size.toString())
        // 리사이클러 뷰에 레이아웃 매니저와 어댑터 설정
            if(! build_t_now_num.equals(check_t_now_num)){
                Log.d("다시시작","Dfdfdfd")
/*

                buildTeamList = ArrayList()
                build_t_now_num =ArrayList()

                try {
                    if (sqlitedb != null) {
                        lateinit var cursor1: Cursor
                        cursor1 = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 2;", null)


                        lateinit var cursor2: Cursor
                        var t_num: Int = -1

                        lateinit var cursor3: Cursor

                        if (cursor1.getCount() != 0) {
                            while (cursor1.moveToNext()) {
                                t_num = cursor1.getInt(cursor1.getColumnIndex("t_num"))
                                cursor2 = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = " + t_num + ";", null)

                                if (cursor2.moveToNext()) {
                                    c_num = cursor2.getInt(cursor2.getColumnIndex("c_num"))
                                    cursor3 = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = " + c_num + ";", null)

                                    if (cursor3.moveToNext())
                                    {
                                        c_photo =  cursor3.getString(cursor3.getColumnIndex("c_photo"))
                                        c_name = cursor3.getString(cursor3.getColumnIndex("c_name"))
                                    }

                                    t_name = cursor2.getString(cursor2.getColumnIndex("t_name")).toString()
                                    t_now_num = cursor2.getInt(cursor2.getColumnIndex("t_now_num"))
                                    t_total_num = cursor2.getInt(cursor2.getColumnIndex("t_total_num"))
                                    t_end_date = cursor2.getString(cursor2.getColumnIndex("t_end_date"))
                                    t_need_part = cursor2.getString(cursor2.getColumnIndex("t_need_part"))

                                }

                                photo_src =  this.resources.getIdentifier(c_photo,"drawable", "com.example.guru2_contestapp")
                                buildTeamList.add(
                                        TeamItem(t_num, photo_src, t_name, c_name,
                                                t_now_num, t_total_num, t_end_date, t_need_part)
                                )
                                build_t_now_num.add(t_now_num)
                            }
                        }
                        cursor1.close()
                        cursor2.close()
                        cursor3.close()
                    }


                }catch(e: Exception){
                    Log.e("Error", e.message.toString())
                } finally{
                    sqlitedb.close()
                    dbManager.close()
                }
 */


                rv_applyTeam.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rv_applyTeam.setHasFixedSize(true)
                rv_applyTeam.adapter = BuildTeamListAdapter(buildTeamList)

                val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
                ft.detach(this)
                ft.attach(this)
                ft.commit()
                }

    }

*/
}