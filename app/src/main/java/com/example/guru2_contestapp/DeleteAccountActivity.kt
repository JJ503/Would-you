package com.example.guru2_contestapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class DeleteAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        val userPwEdit: EditText = findViewById(R.id.userPwEdit)
        val checkBtn: Button = findViewById(R.id.checkBtn)

        //현재 로그인 중인 사용자 지정
        var context: Context = this
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        //DB연결하여, 사용자 pw 불러오기
        lateinit var dbManager: DBManager
        lateinit var sqlitedb: SQLiteDatabase

        var USER_PW: String = ""

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        try {
            if (sqlitedb != null) {
                var cursor: Cursor
                cursor = sqlitedb.rawQuery(
                    "SELECT m_pw FROM member WHERE m_id = '" + USER_ID + "';",
                    null
                )
                if (cursor.getCount() != 0) {
                    if (cursor.moveToNext()) {
                        USER_PW = cursor.getString(cursor.getColumnIndex("m_pw"))
                    }
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        } finally {
            sqlitedb.close()
            dbManager.close()
        }


        // 버튼 클릭시
        checkBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("회원 탈퇴")


            // 비밀번호 일치 여부 확인
            // '현재 비밀번호' 입력 안 했을 때
            if (userPwEdit.getText().toString().equals("") || userPwEdit.getText()
                    .toString() == null
            ) {
                builder.setMessage("비밀번호를 입력하세요.")
                builder.setPositiveButton("확인",null)
            } else {
                // '현재 비밀번호'가 아닐 때
                if (USER_PW != userPwEdit.getText().toString()) {
                    builder.setMessage("비밀번호가 일치하지 않습니다.")
                    builder.setPositiveButton("확인",null)
                } else {
                    // 비밀번호 맞을 경우
                    builder.setMessage("회원 탈퇴를 진행하시겠습니까?\n회원 탈퇴 시, 개인 정보는 삭제되어 복구 불가능합니다.")

                    builder.setNeutralButton("취소", null)
                    builder.setPositiveButton("탈퇴") { dialog, which ->

                         // 현재 로그인 사용자를 저장해놓은 SharedPreferences 값 clear
                        var context: Context = this
                        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
                        val editor : SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("USER_ID", "")
                        editor.commit()

                        //DB에서 해당 사용자 계정 삭제
                        try {
                            if (sqlitedb != null) {
                                sqlitedb = dbManager.writableDatabase
                                sqlitedb.execSQL(("DELETE FROM member WHERE m_id = '" + USER_ID + "';"), null)
                            }
                        } catch (e: Exception) {
                            Log.e("Error", e.message.toString())
                        } finally {
                            sqlitedb.close()
                            dbManager.close()
                        }

                        //로그인 페이지로 이동
                        val intent = Intent (this, MainActivity::class.java)
                        startActivity(intent)

                    }

                }
            }
            builder.show()
        }

    }
}