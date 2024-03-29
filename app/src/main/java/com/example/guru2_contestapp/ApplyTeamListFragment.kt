package com.example.guru2_contestapp

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ApplyTeamListFragment : Fragment() {

    lateinit var dbManager : DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var t_name: String
    lateinit var c_name : String
    lateinit var t_need_part : String
    lateinit var t_end_date : String
    lateinit var c_photo : String
    var photo_src= -1  // 사진 경로

    var t_now_num : Int = -1
    var t_total_num : Int = -1
    var c_num : Int = -1
    var state : Int = -2

    // 리사이클러뷰 어댑터에 넘겨줄 리스트로, 신청한 팀 목록이 담김
    lateinit var applyTeamList : ArrayList<ApplyTeamItem>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v_applyTeamListFragment = inflater.inflate(R.layout.fragment_team_list, container, false)

        applyTeamList=ArrayList()


        // 현재 로그인 중인 사용자 정보 가져오기
        var context: Context = requireContext()
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb =dbManager.readableDatabase


        // 리사이클러뷰에 적용시킬 신청한 팀 정보를 DB에서 가져와 리스트에 저장한다.
        try {
            if (sqlitedb != null) {
                lateinit var cursor1: Cursor
                cursor1 = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state >= -1  AND state < 2;", null)

                lateinit var cursor2 :Cursor
                var t_num :Int = 0

                lateinit var cursor3 :Cursor

                if (cursor1.getCount() != 0) {
                    while (cursor1.moveToNext()) {

                        state = cursor1.getInt(cursor1.getColumnIndex("state"))
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
                        applyTeamList.add(
                            ApplyTeamItem(t_num, photo_src, t_name, c_name,
                                t_now_num, t_total_num, t_end_date, t_need_part, state)
                        )
                    }
                }
                cursor1.close()
                cursor2.close()
                cursor3.close()
            }
        }catch (e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }



        // 리사이클러 뷰에 레이아웃 매니저와 어댑터 설정
        var rv_applyTeam :RecyclerView = v_applyTeamListFragment.findViewById<RecyclerView>(R.id.rv_team)
        rv_applyTeam.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_applyTeam.setHasFixedSize(true)
        rv_applyTeam.adapter = ApplyTeamListAdapter(applyTeamList)


        return v_applyTeamListFragment
    }
}