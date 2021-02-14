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

class CareerTeamListFragment : Fragment() {

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


    lateinit var careerTeamList: ArrayList<TeamItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v_careerList = inflater.inflate(R.layout.fragment_team_list, container, false)

        careerTeamList = ArrayList()

        // 현재 로그인 중인 사용자 정보 가져오기
        var context: Context = requireContext()
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase


        // 리사이클러뷰에 적용시킬 경력 정보를 DB에서 가져와 리스트에 저장한다.
        try {
            if (sqlitedb != null) {
                lateinit var cursor1: Cursor
                cursor1 = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 5;", null)  //  쿼리1

                // refresh
                last_now_num=cursor1.getCount()

                var t_num: Int = 0
                lateinit var cursor2: Cursor
                lateinit var cursor3: Cursor
                if (cursor1.getCount() != 0) {
                    while (cursor1.moveToNext()) {

                        t_num = cursor1.getInt(cursor1.getColumnIndex("t_num"))
                        cursor2 = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = " + t_num + ";", null) //쿼리2

                        if (cursor2.moveToNext()) {
                            // 사진 추가도 해야됨 .
                            c_num = cursor2.getInt(cursor2.getColumnIndex("c_num"))
                            cursor3 = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = " + c_num + ";", null)

                            if (cursor3.moveToNext()) {
                                c_photo = cursor3.getString(cursor3.getColumnIndex("c_photo"))
                                c_name = cursor3.getString(cursor3.getColumnIndex("c_name"))
                            }

                            t_name = cursor2.getString(cursor2.getColumnIndex("t_name")).toString()
                            t_now_num = cursor2.getInt(cursor2.getColumnIndex("t_now_num"))
                            t_total_num = cursor2.getInt(cursor2.getColumnIndex("t_total_num"))
                            t_end_date = cursor2.getString(cursor2.getColumnIndex("t_end_date"))
                            t_need_part = cursor2.getString(cursor2.getColumnIndex("t_need_part"))
                        }
                        photo_src = this.resources.getIdentifier(c_photo, "drawable", "com.example.guru2_contestapp")
                        careerTeamList.add(
                                TeamItem(t_num, photo_src, t_name, c_name,
                                        t_now_num, t_total_num, t_end_date, t_need_part)
                        )
                    }
                }
                cursor1.close()
                cursor2.close()
                cursor3.close()
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        } finally {
            sqlitedb.close()
            dbManager.close()
        }

        // 리사이클러 뷰에 레이아웃 매니저와 어댑터 설정
        var rv_createTeam: RecyclerView = v_careerList.findViewById<RecyclerView>(R.id.rv_team)

        rv_createTeam.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_createTeam.setHasFixedSize(true)
        rv_createTeam.adapter = CareerTeamListAdapter(careerTeamList)


        return v_careerList

    }


    // 직업 정보 혹은 프로필이 변경된 경우 새로고침되도록 한다.
    override fun onResume() {
        super.onResume()

        var select_now_num=0

        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor

        try {
            if(sqlitedb!=null) {
                lateinit var cursor1: Cursor
                cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 5;", null)  //  쿼리1
                if(cursor.count!=0) {
                    select_now_num=cursor.getCount()

                }
                cursor.close()
            }
        }catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }


        if(select_now_num>last_now_num) {
            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
        }

    }


}