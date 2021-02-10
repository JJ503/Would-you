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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ApplyTeamListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v_applyTeamListFragment = inflater.inflate(R.layout.fragment_team_list, container, false)

        // DB에서 정보 불러오기(리사이클러뷰)
        lateinit var dbManager : DBManager
        lateinit var sqlitedb : SQLiteDatabase

        //만든 팀 목록 저장
        lateinit var t_name: String
        lateinit var c_name : String
        lateinit var t_end_date : String
        lateinit var t_need_part : String
        var t_now_num : Int = -1
        var t_total_num : Int = -1
        var c_num : Int = -1
        var state : Int = -2

        lateinit var applyTeamList : ArrayList<ApplyTeam>
        applyTeamList=ArrayList()


        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb =dbManager.readableDatabase
        try {
            if (sqlitedb != null) {
                lateinit var cursor1: Cursor // 쿼리1
                cursor1 = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state >= -1  AND state < 2;", null)  //  쿼리1

                lateinit var cursor2 :Cursor // 쿼리2
                var t_num :Int = 0    // 쿼리1로 얻은 t_num 저장하는 임시변수 (쿼리2의 인자값으로 사용됨)

                lateinit var cursor3 :Cursor // 쿼리3

                if (cursor1.getCount() != 0) {
                    while (cursor1.moveToNext()) {

                        state = cursor1.getInt(cursor1.getColumnIndex("state"))
                        t_num = cursor1.getInt(cursor1.getColumnIndex("t_num"))
                        cursor2 = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = " + t_num + ";", null) //쿼리2

                        if (cursor2.moveToNext()) {
                            // 사진 추가도 해야됨 .
                            c_num = cursor2.getInt(cursor2.getColumnIndex("c_num"))
                            cursor3 = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = " + c_num + ";", null) //쿼리3

                            if (cursor3.moveToNext())   //공모전 이름 가져오기
                            {
                                c_name = cursor3.getString(cursor3.getColumnIndex("c_name"))
                            }


                            t_name = cursor2.getString(cursor2.getColumnIndex("t_name")).toString()
                            t_now_num = cursor2.getInt(cursor2.getColumnIndex("t_now_num"))
                            t_total_num = cursor2.getInt(cursor2.getColumnIndex("t_total_num"))
                            t_end_date = cursor2.getString(cursor2.getColumnIndex("t_end_date"))
                            t_need_part = cursor2.getString(cursor2.getColumnIndex("t_need_part"))

                        }
                        applyTeamList.add(
                                ApplyTeam(t_num, R.drawable.poster_img, t_name, c_name,
                                t_now_num, t_total_num, t_end_date, t_need_part, state)
                        )
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



        var rv_applyTeam :RecyclerView = v_applyTeamListFragment.findViewById<RecyclerView>(R.id.rv_team)

        // 리사이클러 뷰에 레이아웃 매니저와 어댑터 설정
        rv_applyTeam.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL, false)
        rv_applyTeam.setHasFixedSize(true)
        rv_applyTeam.adapter = ApplyTeamAdapter(applyTeamList)



        return v_applyTeamListFragment
    }



}