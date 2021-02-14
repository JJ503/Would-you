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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class BuildTeamListFragment : Fragment() {


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

    var last_now_num=0

    //refresh(새로고침)를 위한 변수
    // 현재 DB의 팀원 수와 화면에 띄워진 팀원의 총
    var total_now_num=0
    var check_now_num=0

    // 리사이클러뷰
   lateinit var  rv_applyTeam: RecyclerView

    // 리사이클러뷰 어댑터에 넘겨줄 리스트로, 만든 팀 목록이 담김
    var buildTeamList: ArrayList<TeamItem> = ArrayList<TeamItem>()
    var build_t_now_num : ArrayList<Int> =  ArrayList<Int>()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        //
        total_now_num=0

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

                        //새로고침을 위해 현재 모인 모든 팀원의 수를 구한다.
                        total_now_num+=t_now_num

                        // DB에는 사진 이름이 저장되어있으므로, 적용시키기 위해서는 resource 경로를 얻어온다.
                        photo_src =  this.resources.getIdentifier(c_photo,"drawable", "com.example.guru2_contestapp")
                        // 리스트에 db에서 불러온 내용들을 담는다.
                        buildTeamList.add(TeamItem(t_num, photo_src, t_name, c_name,
                                        t_now_num, t_total_num, t_end_date, t_need_part))
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


        // 리사이클러뷰로 띄우기 위해, 만든 커스텀 어댑터 BuildTeamListAdapter와 연결해준다.
        rv_applyTeam = v_buildTeamList.findViewById<RecyclerView>(R.id.rv_team)

        rv_applyTeam.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_applyTeam.adapter = BuildTeamListAdapter(buildTeamList)
        rv_applyTeam.adapter?.notifyDataSetChanged()



        return v_buildTeamList
    }




    // 신청한 팀 정보가 달라진 경우 새로고침을 해준다.
    override fun onResume() {
        super.onResume()


        // 현재 db의 팀원 총 명수를 가져온다.
        check_now_num =0

        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        lateinit var cursor1 : Cursor
        lateinit var cursor2 :Cursor

        try {
            if(sqlitedb!=null) {
                cursor1 = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 2;", null)

                var t_num: Int = -1

                if (cursor1.getCount() != 0) {
                    while (cursor1.moveToNext()) {
                        t_num = cursor1.getInt(cursor1.getColumnIndex("t_num"))
                        cursor2 = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = " + t_num + ";", null)

                        if (cursor2.moveToNext()) {
                            check_now_num +=cursor2.getInt(cursor2.getColumnIndex("t_now_num"))
                        }
                    }
                }
                cursor1.close()
                cursor2.close()
            }
        }catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }


        //만약 현재 화면에 띄워진 팀원 총 명수와, 현재 db에 가져온 팀원 총 명수가 다르다면
        // 새로 고침을 해준다.
        if(total_now_num!=check_now_num) {
            rv_applyTeam.adapter?.notifyDataSetChanged()
            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
        }

    }

}