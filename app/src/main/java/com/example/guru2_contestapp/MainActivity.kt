package com.example.guru2_contestapp


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

// 로그인 페이지 검 시작 페이지
class MainActivity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var main : ConstraintLayout
    lateinit var idEditText : EditText
    lateinit var pwEditText : EditText
    lateinit var signUpText : TextView
    lateinit var searchText : TextView
    lateinit var loginButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 액션바 설정
        supportActionBar?.elevation = 3f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.app_title)+"</font>"))

        main = findViewById(R.id.Jmain)
        idEditText = findViewById(R.id.JidEditText)
        pwEditText = findViewById(R.id.JpwEditText)
        searchText = findViewById<TextView>(R.id.JsearchText)
        signUpText = findViewById<TextView>(R.id.JsignUpText)
        loginButton = findViewById<Button>(R.id.JloginButton)

        // editText 외 다른 부분을 터치하면 키보드 자동 숨김
        main.setOnClickListener {
            CloseKeyboard()
        }

        // DB와 연결
        dbManager = DBManager(this, "ContestAppDB", null, 1)

        // 회원가입을 눌렀을 때 : 회원가입 페이지로 이동
        signUpText.setOnClickListener {
            // 회원가입 진행을 하다가 나왔다가 다시 들어가면 정보가 load 되지 않도록 삭제해줌
            var pref = this.getSharedPreferences("join", 0)
            var editor = pref.edit()
            editor.remove("JOIN_PROFILE")
            editor.remove("JOIN_NAME")
            editor.remove("JOIN_ID")
            editor.remove("JOIN_PASSWORD")
            editor.remove("JOIN_PHONE")
            editor.remove("JOIN_BIRTH")
            editor.remove("JOIN_EMAIL")
            editor.commit()

            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // ID/PW 찾기를 눌렀을 때 : ID/PW 찾기 페이지로 이동
        searchText.setOnClickListener {
            val intent = Intent(this, SearchIdPwActivity::class.java)
            startActivity(intent)
        }

        // 로그인
        loginButton.setOnClickListener {
            var USER_ID = idEditText.text.toString()
            var USER_PW = pwEditText.text.toString()
            sqlitedb = dbManager.readableDatabase

            try{
                var cursor : Cursor
                cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '${USER_ID}';", null)

                cursor.moveToFirst()

                // 계정이 없는 경우
                if (cursor.getCount() != 1){
                    Toast.makeText(this, "존재하지 않는 계정입니다.", Toast.LENGTH_SHORT).show()
                } else{  // 계정이 있는 경우
                    // 로그인 성공
                    if (USER_PW == cursor.getString(cursor.getColumnIndex("m_pw")).toString()){
                        saveData(idEditText.text.toString())

                        // 앱 내에서 어느 Activity에서든 사용할 수 있도록 USER_ID 키에 로그인한 회원의 id를 저장
                        val sharedPreferences : SharedPreferences = getSharedPreferences("userid", MODE_PRIVATE)
                        val editor : SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("USER_ID", USER_ID)
                        editor.commit()

                        val intent = Intent(this, NavigationActivity::class.java)
                        startActivity(intent)
                    } else{  // 비밀번호 틀림
                        Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                dbManager.close()
                sqlitedb.close()
            }
        }
    }

    private fun saveData(user_id: String){
        var pref = this.getPreferences(0)
        var editor = pref.edit()

        editor.putString("KEY_USERID", idEditText.text.toString()).apply()
    }

    fun CloseKeyboard()
    {
        var view = this.currentFocus

        if(view != null)
        {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}