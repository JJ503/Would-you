package com.example.guru2_contestapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ContestFragment : Fragment() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var contestListView: RecyclerView
    lateinit var searchNum: TextView
    lateinit var searchET: AutoCompleteTextView
    lateinit var searchBtn: ImageButton
    lateinit var contestListArray: ArrayList<ContestListViewItem>
    lateinit var contestItem: ContestListViewItem
    lateinit var contestSearchArray: MutableList<String>
    lateinit var swipeRefreshLayout: SwipeRefreshLayout


    lateinit var c_photo: String
    lateinit var c_name: String
    lateinit var c_host: String
    lateinit var c_startDay: String
    lateinit var c_endDay: String
    var str_search=""
    var c_num=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 공모전 목록 배열
        contestListArray= arrayListOf<ContestListViewItem>()
        val v_contest = inflater.inflate(R.layout.fragment_contest, null)

        // 공모전 검색
        searchBtn=v_contest.findViewById(R.id.WprofileEditButton)
        searchET=v_contest.findViewById(R.id.WsearchEditText)


        // 검색 버튼 클릭 시, 현재 Fragment를 새로고침하여 변경된 contestListArray이 적용되게 함
        searchBtn.setOnClickListener {
            str_search=searchET.text.toString()
            val contestListAdapter= activity?.let { ContestListViewAdapter(contestListArray) }
            if (contestListAdapter != null) {
                contestListAdapter.notifyDataSetChanged()
            }
            contestListView.adapter=contestListAdapter

            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
        }
        contestSearchArray=ArrayList<String>()

        //공모전 목록 가져와 자동완성 목록에 추가
        //DB의 contest 테이블에서 공모전 이름 가져와 목록에 추가
        dbManager = DBManager(context, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        try {
            if (sqlitedb!=null){
                cursor=sqlitedb.rawQuery("SELECT c_name FROM contest;", null)
                if(cursor.count!=0){
                    var arrayCName: String
                    while(cursor.moveToNext()){
                        arrayCName=cursor.getString(cursor.getColumnIndex("c_name"))
                        contestSearchArray.add(arrayCName)
                    }
                }
                cursor.close()
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }

        var adapter=ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, contestSearchArray)
        searchET.setAdapter(adapter)

        //DB
        dbManager= DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb=dbManager.readableDatabase
        try {
            if(sqlitedb!=null){
                // 검색창에 아무것도 입력하지 않은 경우, 모든 공모전을 보여줌
                // 검색창에 문자열을 입력하면 해당 공모전만 해당되는 공모전만 contestListArray에 추가
                if(str_search!=""){
                    cursor=sqlitedb.rawQuery("SELECT * FROM contest WHERE c_name = '"+str_search+"';", null)
                }else{
                    cursor=sqlitedb.rawQuery("SELECT * FROM contest ORDER BY c_start;", null)
                }

                if(cursor.count!=0){
                    while(cursor.moveToNext()){
                        c_photo=cursor.getString(cursor.getColumnIndex("c_photo"))
                        c_num=cursor.getInt(cursor.getColumnIndex("c_num"))
                        c_name=cursor.getString(cursor.getColumnIndex("c_name"))
                        c_host=cursor.getString(cursor.getColumnIndex("c_host"))
                        c_startDay=cursor.getString(cursor.getColumnIndex("c_start"))
                        c_endDay=cursor.getString(cursor.getColumnIndex("c_end"))

                        var photo_src=this.resources.getIdentifier(c_photo,"drawable", "com.example.guru2_contestapp")
                        contestItem=ContestListViewItem(c_num, photo_src, c_name, c_host, c_startDay, c_endDay)
                        contestListArray.add(contestItem)
                    }
                    cursor.close()
                }
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }

        contestListView=v_contest.findViewById<RecyclerView>(R.id.WcontestListView)
        contestListView.adapter = ContestListViewAdapter(contestListArray)

        // 당겨서 새로고침
        swipeRefreshLayout=v_contest.findViewById(R.id.WswipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            val contestListAdapter= activity?.let { ContestListViewAdapter(contestListArray) }
            if (contestListAdapter != null) {
                contestListAdapter.notifyDataSetChanged()
            }
            contestListView.adapter=contestListAdapter

            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
            swipeRefreshLayout.isRefreshing=false
        }

        // 검색 결과 수 TextView 값 = Item 수
        searchNum=v_contest.findViewById(R.id.WsearchNumTextView)
        val contestListAdapter= activity?.let { ContestListViewAdapter(contestListArray) }
        contestListView.adapter=contestListAdapter
        if (contestListAdapter != null) {
            searchNum.text= contestListAdapter.itemCount.toString()
        }else{
            searchNum.text="0"
        }

        return v_contest
    }
}