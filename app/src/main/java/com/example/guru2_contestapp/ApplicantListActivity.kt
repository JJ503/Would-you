package com.example.guru2_contestapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ApplicantListActivity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var applicantRecycler : RecyclerView

    lateinit var listArray : ArrayList<ApplicantListItem>         // SQLite에서 가져온 원본 데이터 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_list)

        listArray = ArrayList()

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        applicantRecycler = findViewById(R.id.JapplicantRecycler)

        var t_num = -1

        if (intent.hasExtra("t_num")) {
            t_num = intent.getIntExtra("t_num", -1)
        } else {
            Toast.makeText(this, "전달된 값이 없습니다", Toast.LENGTH_SHORT).show()
        }

        try {
            var cursor : Cursor
            cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${t_num} AND state != 2", null)

            while (cursor.moveToNext()){
                var m_id = cursor.getString(cursor.getColumnIndex("m_id")).toString()

                var cursor2 : Cursor
                cursor2 = sqlitedb.rawQuery("SELECT m_name FROM member WHERE m_id = '${m_id}'", null)

                cursor2.moveToFirst()
                var m_name = cursor2.getString(cursor2.getColumnIndex("m_name")).toString()
                listArray.add(ApplicantListItem(t_num, m_id, m_name))
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }

        // 리사이클러 뷰에 레이아웃 매니저와 어댑터 설정
        applicantRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        applicantRecycler.setHasFixedSize(true)
        applicantRecycler.adapter = ApplicantListAdapter(listArray)
    }
}