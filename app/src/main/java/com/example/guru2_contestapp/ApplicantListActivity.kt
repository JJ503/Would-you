package com.example.guru2_contestapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

// 팀 신청자 목록 페이지
class ApplicantListActivity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var applicantRecycler : RecyclerView
    lateinit var announStatusText : TextView

    lateinit var listArray : ArrayList<ApplicantListItem>         // SQLite에서 가져온 원본 데이터 리스트

    lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_list)

        // 액션바 설정
        supportActionBar?.elevation = 3f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow2)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_applicantList)+"</font>"))

        // 현재 팀 번호와 팀 상태의 키 값을 불러옴
        val sharedPreferences: SharedPreferences = getSharedPreferences("t_num", AppCompatActivity.MODE_PRIVATE)
        val t_num = sharedPreferences.getInt("t_num", -1)
        var t_endStatus = sharedPreferences.getInt("t_endStatus", -1)   //모집 종료면 0, 모집 중이면 1

        // 리스트 배열 초기화
        listArray = ArrayList()

        // DB 연결
        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        applicantRecycler = findViewById(R.id.JapplicantRecycler)
        announStatusText = findViewById(R.id.JannounStatusText)


        // 선택한 팀의 신청자 목록 출력
        try {
            var t_cursor : Cursor
            t_cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = ${t_num}", null)
            t_cursor.moveToFirst()
            val t_complete :Int = t_cursor.getInt(t_cursor.getColumnIndex("t_complete"))

            // 완료된 팀 : 팀원만 보임
            if (t_complete == 1){
                var tm_cursor : Cursor
                tm_cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${t_num} AND state = 5", null)

                if (tm_cursor.getCount() > 0){
                    t_endStatus = 5

                    announStatusText.text = "모집이 종료되었습니다."
                    announStatusText.visibility = VISIBLE

                    while (tm_cursor.moveToNext()){
                        var m_id = tm_cursor.getString(tm_cursor.getColumnIndex("m_id")).toString()

                        var m_cursor : Cursor
                        m_cursor = sqlitedb.rawQuery("SELECT m_name FROM member WHERE m_id = '${m_id}'", null)
                        m_cursor.moveToFirst()

                        var m_name = m_cursor.getString(m_cursor.getColumnIndex("m_name")).toString()
                        listArray.add(ApplicantListItem(t_num, t_endStatus, m_id, m_name))
                    }
                } else {
                    announStatusText.text = "신청자가 없습니다."
                    announStatusText.visibility = VISIBLE
                }

                if (t_endStatus == 0){
                    announStatusText.text = "모집이 종료되었습니다."
                    announStatusText.visibility = VISIBLE
                }

            } else{  // 완료 전인 팀 : 위에 완료 버튼이 보여야 하고 신청자 모두 보임
                var tm_cursor : Cursor
                tm_cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${t_num} AND state != 2 ORDER BY state DESC", null)

                if (tm_cursor.getCount() > 0){
                    announStatusText.visibility = GONE

                    while (tm_cursor.moveToNext()) {
                        var m_id = tm_cursor.getString(tm_cursor.getColumnIndex("m_id")).toString()

                        var m_cursor: Cursor
                        m_cursor = sqlitedb.rawQuery("SELECT m_name FROM member WHERE m_id = '${m_id}'", null)

                        m_cursor.moveToFirst()
                        var m_name = m_cursor.getString(m_cursor.getColumnIndex("m_name")).toString()
                        listArray.add(ApplicantListItem(t_num, t_endStatus, m_id, m_name))
                    }
                } else {
                    announStatusText.text = "신청자가 없습니다."
                    announStatusText.visibility = VISIBLE
                }

                if (t_endStatus == 0){
                    announStatusText.text = "모집이 종료되었습니다."
                    announStatusText.visibility = VISIBLE
                }
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


        // 당겨서 새로고침
        swipeRefresh=findViewById(R.id.JswipeRefresh)
        swipeRefresh.setOnRefreshListener {
            this.recreate()
            swipeRefresh.isRefreshing = false
        }
    }

    // 모집 중인 팀만 상단에 완료 버튼이 뜸
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("t_num", AppCompatActivity.MODE_PRIVATE)
        val t_endStatus = sharedPreferences.getInt("t_endStatus", -1)  //모집 종료면 0, 모집 중이면 1

        if (t_endStatus == 1){
            menuInflater.inflate(R.menu.menu_applicant_list, menu)
        }
        return true
    }

    // 완료 버튼을 누르면 수락 상태의 사람들은 상태를 0에서 5(경력)로 변경하고 거절 혹은 아무 처리도 안 된 사람들은 -1로 변경한다
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.teamComplete){
            val sharedPreferences: SharedPreferences = getSharedPreferences("t_num", AppCompatActivity.MODE_PRIVATE)
            val t_num = sharedPreferences.getInt("t_num", -1)
            val t_endStatus = sharedPreferences.getInt("t_endStatus", -1)  //모집 종료면 0, 모집 중이면 1

            dbManager = DBManager(this, "ContestAppDB", null, 1)
            sqlitedb = dbManager.writableDatabase

            var cursor : Cursor
            cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = ${t_num};", null)

            if (cursor.getCount() == 1){
                sqlitedb.execSQL("UPDATE team SET t_complete = 1 WHERE t_num = ${t_num};")

                sqlitedb.execSQL("UPDATE teamManage SET state = 5 WHERE t_num = ${t_num} AND state = 1 OR state = 2;")

                sqlitedb.execSQL("UPDATE teamManage SET state = -1 WHERE t_num = ${t_num} AND state = 0;")

                // 바로 반영될 수 있도록 새로고침
                this.recreate()

            } else {
                Toast.makeText(this, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
            }
        } else if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}