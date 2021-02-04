package com.example.guru2_contestapp

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var idEditText : EditText
    lateinit var pwEditText : EditText
    lateinit var signUpText : TextView
    lateinit var searchText : TextView
    lateinit var loginButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //idEditText = findViewById(R.id.JidEditText)
        //pwEditText = findViewById(R.id.JpwEditText)
        //searchText = findViewById<TextView>(R.id.JsearchText)
        //signUpText = findViewById<TextView>(R.id.JsignUpText)
        loginButton = findViewById<Button>(R.id.JloginButton)
        loginButton.setOnClickListener {
            val intent= Intent(this, NavigationActivity::class.java)
            startActivity(intent)
        }
        /*dbManager = DBManager(this, "ContestAppDB", null, 1)

        signUpText.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        searchText.setOnClickListener {
            val intent = Intent(this, SearchIdPw::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            var USER_ID = idEditText.text.toString()
            var USER_PW = pwEditText.text.toString()
            sqlitedb = dbManager.readableDatabase

            try{
                var cursor : Cursor
                cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE id = '${USER_ID}';", null)

                cursor.moveToFirst()

                if (cursor.getCount() != 1){
                    Toast.makeText(this, "존재하지 않는 계정입니다.", Toast.LENGTH_SHORT).show()
                } else{
                    if (USER_PW == cursor.getString(cursor.getColumnIndex("pw")).toString()){
                        saveData(idEditText.text.toString())

                        val sharedPreferences : SharedPreferences = getSharedPreferences("userid", MODE_PRIVATE)
                        val editor : SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("USER_ID", USER_ID)
                        editor.commit()

                        val intent = Intent(this, NavigationActivity::class.java)
                        //intent.putExtra("id", idEditText.text.toString())
                        startActivity(intent)
                    } else{
                        Toast.makeText(this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
            }
        }*/
    }

    /*private fun saveData(user_id: String){
        var pref = this.getPreferences(0)
        var editor = pref.edit()

        editor.putString("KEY_USERID", idEditText.text.toString()).apply()
    }

    private fun loadData(){
        var pref = this.getPreferences(0)
        var user_id = pref.getString("KEY_USERID", "")
    }*/
}