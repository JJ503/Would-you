package com.example.guru2_contestapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ApplicantList : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var recycler : RecyclerView

    lateinit var listArray : ArrayList<ApplicantListData>         // SQLite에서 가져온 원본 데이터 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_list)

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