package com.example.guru2_contestapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction

class ContestFragment : Fragment() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var contestListView: ListView
    lateinit var searchNum: TextView
    lateinit var searchET: EditText
    lateinit var searchBtn: ImageButton

    var str_search=""
    var c_num=0
    lateinit var c_name: String
    lateinit var c_host: String
    lateinit var c_startDay: String
    lateinit var c_endDay: String

    lateinit var contestListArray: ArrayList<ContestListViewItem>
    lateinit var contestItem: ContestListViewItem

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        contestListArray= arrayListOf<ContestListViewItem>()


        var v_contest = inflater.inflate(R.layout.fragment_contest, null)

        // 공모전 검색
        searchBtn=v_contest.findViewById(R.id.WsearchButton)
        searchET=v_contest.findViewById(R.id.WsearchEditText)

        searchBtn.setOnClickListener {
            str_search=searchET.text.toString()

            val contestListAdapter= activity?.let { ContestListViewAdapter(it, contestListArray) }
            if (contestListAdapter != null) {
                contestListAdapter.notifyDataSetChanged()
            }
            contestListView.adapter=contestListAdapter

            var ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
        }

        //DB
        dbManager= DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb=dbManager.readableDatabase

        var cursor: Cursor

        if(str_search!=""){
            cursor=sqlitedb.rawQuery("SELECT * FROM contest WHERE c_name = '"+str_search+"';", null)
        }else{
            cursor=sqlitedb.rawQuery("SELECT * FROM contest;", null)
        }
        var num: Int=0

        while(cursor.moveToNext()){
            //var c_photo=cursor.getString(cursor.getColumnIndex("c_photo")).toString()
            c_num=cursor.getInt(cursor.getColumnIndex("c_num"))
            c_name=cursor.getString(cursor.getColumnIndex("c_name")).toString()
            c_host=cursor.getString(cursor.getColumnIndex("c_host")).toString()
            c_startDay=cursor.getString(cursor.getColumnIndex("c_start")).toString()
            c_endDay=cursor.getString(cursor.getColumnIndex("c_end")).toString()

            contestItem=ContestListViewItem(c_num, c_name, c_name,c_host,c_startDay,c_endDay)
            contestListArray.add(contestItem)
        }
        cursor.close()
        dbManager.close()
        sqlitedb.close()


        // 공모전 리스트뷰
        contestListView=v_contest.findViewById<ListView>(R.id.WcontestListView)

        contestListView.setOnItemClickListener { parent, view, position, id ->
            activity?.let {
                val intent= Intent(activity, ContestDetailActivity::class.java)
                intent.putExtra("intent_c_num", contestListArray[position].num)
                startActivity(intent)
            }
        }

        searchNum=v_contest.findViewById(R.id.WsearchNumTextView)

        val contestListAdapter= activity?.let { ContestListViewAdapter(it, contestListArray) }
        contestListView.adapter=contestListAdapter

        if (contestListAdapter != null) {
            searchNum.text= contestListAdapter.count.toString()
        }else{
            searchNum.text="0"
        }

        return v_contest
    }
}