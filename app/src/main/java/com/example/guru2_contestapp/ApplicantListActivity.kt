package com.example.guru2_contestapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ApplicantListActivity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var recycler : RecyclerView

    lateinit var listArray : ArrayList<ApplicantListData>         // SQLite에서 가져온 원본 데이터 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_list)

        //BuildTeamListFragment에서 t_num을 전달받는다.
        val t_num :Int = intent.getIntExtra("t_num",-1)
        //Toast.makeText(this,"t_num : "+t_num,Toast.LENGTH_SHORT).show()
        Log.d("/--------t_num=--------/",t_num.toString())

        listArray = ArrayList()

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        recycler = findViewById(R.id.JapplicantRecycler)


        try {
            var cursor: Cursor
            cursor = sqlitedb.rawQuery("SELECT * FROM member;", null)

            while (cursor.moveToNext()){
                var a_name = cursor.getString(cursor.getColumnIndex("name")).toString()
                listArray.add(ApplicantListData(a_name))
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
        }

        // 리사이클러 뷰에 레이아웃 매니저와 어댑터 설정
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.setHasFixedSize(true)


        val adapter = ApplicantListAdapter(listArray) { item ->
            //val intent = Intent(this, ApplicantPager::class.java)
            //startActivity(intent)
        }

        recycler.adapter = adapter
    }
}