package com.example.guru2_contestapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext

class DeveloperSupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer_support)

        var delevoper_supportBtn: Button = findViewById<Button>(R.id.delevoper_supportBtn)
        var bug_reportBtn: Button = findViewById<Button>(R.id.bug_reportBtn)


        // 개발자 문의 버튼 클릭시, 메일 화면으로 넘어감
        delevoper_supportBtn.setOnClickListener {

            lateinit var sqlitedb : SQLiteDatabase
            var dbManager: DBManager = DBManager(this, "ContestAppDB", null, 1)

            //현재 로그인 중인 사용자 지정
            var context: Context = this
            val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
            var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

            try {
                sqlitedb = dbManager.readableDatabase

                if (sqlitedb != null) {
                    var cursor: Cursor
                    cursor = sqlitedb.rawQuery(
                        "SELECT * FROM member WHERE m_id = '${USER_ID}';",
                        null
                    )
                    cursor.moveToFirst()

                    if (cursor.getCount() == 1) {

                        var USER_EMAIL :String = cursor.getString(cursor.getColumnIndex("m_email"))
                        var USER_NAME = cursor.getString(cursor.getColumnIndex("m_name"))
                        var mailTitle = "[ 개발자 문의 ] 개발자 문의_${USER_ID}"
                        var mailBody = "* 문의자 ID : ${USER_ID}\n" +
                                "* 문의 제목 : (문의 내용 요약)\n" +
                                "* 문의 내용 : (문의 내용)\n"


                        Log.d("/====email===/",USER_EMAIL)

                        val email = Intent(Intent.ACTION_SEND)
                        email.type = "plain/text"
                        email.putExtra(Intent.EXTRA_EMAIL, "contestApp@gmail.com") // 받는사람 이메일
                        email.putExtra(Intent.EXTRA_SUBJECT, mailTitle) //제목
                        email.putExtra(Intent.EXTRA_TEXT, mailBody) //내용
                        startActivity(email)

                    } else {
                        Toast.makeText(this, "개발자 문의에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            } finally {
                sqlitedb.close()
            }
        }



        // 버그 신고 버튼 클릭시, 메일 화면으로 넘어감
        bug_reportBtn.setOnClickListener {

            lateinit var sqlitedb : SQLiteDatabase
            var dbManager: DBManager = DBManager(this, "ContestAppDB", null, 1)

            //현재 로그인 중인 사용자 지정
            var context: Context = this
            val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
            var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

            try {
                sqlitedb = dbManager.readableDatabase

                if (sqlitedb != null) {
                    var cursor: Cursor
                    cursor = sqlitedb.rawQuery(
                        "SELECT * FROM member WHERE m_id = '${USER_ID}';",
                        null
                    )
                    cursor.moveToFirst()

                    if (cursor.getCount() == 1) {

                        var USER_EMAIL :String = cursor.getString(cursor.getColumnIndex("m_email"))
                        var USER_NAME = cursor.getString(cursor.getColumnIndex("m_name"))
                        var mailTitle = "[ 버그 신고 ] 버그 신고_${USER_ID}"
                        var mailBody = "* 버그 신고자 ID : ${USER_ID}\n" +
                                "* 버그 종류 : (ex. 데이터 오류/ 화면 꺼짐/ 강제종료 / 기타)\n" +
                                "* 버그 내용 : (ex. 메인 페이지에서 신청한 팀 목록이 안 보여요.)\n" +
                                "* 버그 발생 일시 : (ex.2020.02.11/23:34)\n" +
                                "* 버그 발생한 기종(환경) : (ex.갤럭시 노트 10)\n"


                        Log.d("/====email===/",USER_EMAIL)

                        val email = Intent(Intent.ACTION_SEND)
                        email.type = "plain/text"
                        email.putExtra(Intent.EXTRA_EMAIL, "contestApp@gmail.com") // 받는사람 이메일
                        email.putExtra(Intent.EXTRA_SUBJECT, mailTitle) //제목
                        email.putExtra(Intent.EXTRA_TEXT, mailBody) //내용
                        startActivity(email)

                    } else {
                        Toast.makeText(this, "버그 신고에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            } finally {
                sqlitedb.close()
            }
        }









    }

    }
