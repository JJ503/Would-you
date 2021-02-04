package com.cookandroid.guru2_joinus

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class createTeamFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    // 프래그먼트 생성시 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    // 프래그먼트 첫 호출시 , 프래그먼트에 UI를 그리기 위해 view 반환
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var preView = inflater.inflate(R.layout.fragment_create_team, container, false)

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
        var t_num : Int =-1
        var c_num: Int =-1


        lateinit var teamList : ArrayList<Team>
        teamList=ArrayList()

        var USER_ID:String="sPPong123"  // 현재 사용자라 가정 (이건 나중에 SESSION 작업 필요)
        dbManager = DBManager(requireContext(), "ContestAppDB", null, 1)
        sqlitedb =dbManager.readableDatabase

        var cursor1: Cursor // 쿼리1
        cursor1 = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE id = '" + USER_ID + "' AND state == 2;", null)  //

        lateinit var cursor2 :Cursor // 쿼리2
        lateinit var cursor3 :Cursor // 쿼리3

        // 사진 추가도 해야됨 .
        while (cursor1.moveToNext()) {

            t_num  = cursor1.getInt(cursor1.getColumnIndex("t_num"))

            cursor2 = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = " + t_num + ";", null) //쿼리2
            if(cursor2.moveToNext()) {  // team에서 해당 팀 정보 가져오기

                c_num = cursor2.getInt(cursor2.getColumnIndex("c_num"))
                cursor3 = sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = " + c_num + ";", null) //쿼리3

                if(cursor3.moveToNext())   //공모전 이름 가져오기
                {
                    c_name = cursor3.getString(cursor3.getColumnIndex("c_name"))
                }

                t_name = cursor2.getString(cursor2.getColumnIndex("t_name")).toString()
                t_now_num = cursor2.getInt(cursor2.getColumnIndex("t_now_num"))
                t_total_num = cursor2.getInt(cursor2.getColumnIndex("t_total_num"))
                t_end_date = cursor2.getString(cursor2.getColumnIndex("t_end_date"))
                t_need_part = cursor2.getString(cursor2.getColumnIndex("t_need_part"))


            }

            teamList.add(  Team(R.drawable.poster_img,t_name,c_name,
                    t_now_num,t_total_num,t_end_date,t_need_part)
            )

        }

        cursor1.close()
        cursor2.close()
        cursor3.close()
        sqlitedb.close()
        dbManager.close()



        var rv_createTeam : RecyclerView = preView.findViewById<RecyclerView>(R.id.rv_createTeam)
        rv_createTeam.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_createTeam.setHasFixedSize(true)

        rv_createTeam.adapter = TeamAdapter(teamList)


        return preView
    }


    // 프래그먼트 생성가능, 인자로 전달 가능 .
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                applyTeamFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}