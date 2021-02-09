package com.example.guru2_contestapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TeamFragment : Fragment() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var teamListView: ListView
    lateinit var searchNum: TextView
    lateinit var addTeamFAb: FloatingActionButton
    lateinit var searchET: EditText
    lateinit var searchBtn: ImageButton

    var str_search=""
    var t_num=0
    lateinit var t_name: String
    var tc_num= 0
    lateinit var c_name: String
    lateinit var t_endDate: String
    lateinit var t_need_part: String
    var t_total_num=0
    var t_now_num=0
    var search_num=0
    var select_num=0

    lateinit var teamListArray: ArrayList<TeamListViewItem>
    lateinit var teamItem: TeamListViewItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        teamListArray= arrayListOf<TeamListViewItem>()

        val v_team = inflater.inflate(R.layout.fragment_team, null)
        teamListView=v_team.findViewById(R.id.WteamListView)
        searchNum=v_team.findViewById(R.id.WsearchNumTextView)
        addTeamFAb=v_team.findViewById(R.id.WteamAddFab)
        searchBtn=v_team.findViewById(R.id.WsearchTeamButton)
        searchET=v_team.findViewById(R.id.WsearchEditText)

        //검색 버튼 클릭
        searchBtn.setOnClickListener {
            str_search=searchET.text.toString()

            val teamListViewAdapter=activity?.let{ TeamListViewAdapter(it, teamListArray) }
            if(teamListViewAdapter!=null){
                teamListViewAdapter.notifyDataSetChanged()
            }
            teamListView.adapter=teamListViewAdapter

            // c_num 찾기
            dbManager= DBManager(activity, "ContestAppDB", null, 1)
            sqlitedb=dbManager.readableDatabase
            val cursor: Cursor

            cursor=sqlitedb.rawQuery("SELECT c_num FROM contest WHERE c_name = '"+str_search+"';", null)
            if(cursor.moveToNext()){
                tc_num=cursor.getInt(cursor.getColumnIndex("c_num"))
            }

            select_num=cursor.count
            if(select_num==0){
                tc_num=0
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
        }

        // 검색창에 아무것도 입력하지 않으면 DB에서 모든 팀 정보를 가져오고
        // 검색창에 문자열이 있으면 해당 팀만 가져온다.
        dbManager= DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb=dbManager.readableDatabase
        val cursor: Cursor

        if(str_search!=""){
            cursor=sqlitedb.rawQuery("SELECT * FROM team WHERE c_num = '"+tc_num+"';", null)
        }else{
            cursor=sqlitedb.rawQuery("SELECT * FROM team ORDER BY t_end_date DESC;", null)
        }

        var cursor2: Cursor
        while(cursor.moveToNext()){
            //var c_photo=cursor.getString(cursor.getColumnIndex("c_photo")).toString()
            t_num=cursor.getInt(cursor.getColumnIndex("t_num"))
            t_name=cursor.getString(cursor.getColumnIndex("t_name")).toString()
            t_need_part=cursor.getString(cursor.getColumnIndex("t_need_part")).toString()
            t_endDate=cursor.getString(cursor.getColumnIndex("t_end_date")).toString()
            t_total_num=cursor.getInt(cursor.getColumnIndex("t_total_num"))
            t_now_num=cursor.getInt(cursor.getColumnIndex("t_now_num"))
            tc_num=cursor.getInt(cursor.getColumnIndex("c_num"))

            // team 테이블의 tc_num(공모전 번호)를 가져와 contest 테이블에서 해당 공모전 이름을 가져온다.
            cursor2=sqlitedb.rawQuery("SELECT c_name FROM contest WHERE c_num = '"+tc_num+"';", null)
            if(cursor2.moveToNext()){
                c_name=cursor2.getString(cursor2.getColumnIndex("c_name")).toString()
            }
            cursor2.close()

            teamItem=TeamListViewItem(t_num, "photo", t_name, c_name, t_endDate, t_need_part, t_total_num, t_now_num)
            teamListArray.add(teamItem)
        }

        select_num=cursor.count

        cursor.close()
        dbManager.close()
        sqlitedb.close()

        val teamListAdapter= activity?.let {
            TeamListViewAdapter(it, teamListArray)
        }
        teamListView.adapter=teamListAdapter

        if (teamListAdapter != null) {
            searchNum.text= teamListAdapter.count.toString()
            search_num=teamListAdapter.count
        }else{
            searchNum.text="0"
        }

        // 팀 목록에서 팀을 선택하면 intent로 팀 번호를 팀 상세 페이지로 넘긴다.
        teamListView.setOnItemClickListener { parent, view, position, id ->
            activity?.let {
                val intent= Intent(activity, TeamDetailActivity::class.java)
                intent.putExtra("intent_t_num", teamListArray[position].teamNum)
                startActivity(intent)
            }
        }

        // 팀 추가 버튼(FloatingActionButton)을 누르면 팀 생성페이지로 이동
        addTeamFAb.setOnClickListener {
            val intent= Intent(activity, BuildTeamActivity::class.java)
            startActivity(intent)
        }

        return v_team
    }

    override fun onResume() {
        super.onResume()
        // 팀 추가 버튼으로 팀을 생성하고 다시 돌아왔을 때, 추가한 팀에 대한 정보가 바로 리스트에 적용되도록
        // DB에 있는 레코드 수와 현재 리스트 Item 수를 비교해 레코드 수가 많으면 현재 Fragment를 새로고침 하도록 한다.
        dbManager= DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb=dbManager.readableDatabase
        val cursor: Cursor
        if(str_search!=""){
            cursor=sqlitedb.rawQuery("SELECT * FROM team WHERE c_num = '"+tc_num+"';", null)
        }else{
            cursor=sqlitedb.rawQuery("SELECT * FROM team ORDER BY t_end_date DESC;", null)
        }
        select_num=cursor.count

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        Log.i("-----------search_num -------", search_num.toString())
        Log.i("-----------select_num -------", select_num.toString())
        if((search_num+1)<=select_num){
            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
        }
    }
}