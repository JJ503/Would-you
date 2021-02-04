package com.example.guru2_contestapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

class SignUp2 : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var phoneEditText : EditText
    lateinit var overlapButton2 : Button
    lateinit var birthEditText : EditText
    lateinit var birthTextView : TextView
    lateinit var emailEditText : EditText
    lateinit var emailTextView : TextView
    lateinit var nextButton2 : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        phoneEditText = findViewById<EditText>(R.id.JphoneEditTextText)
        overlapButton2 = findViewById<Button>(R.id.JoverlapButton2)
        birthEditText = findViewById<EditText>(R.id.JbirthEditText)
        birthTextView = findViewById<TextView>(R.id.birthTextView)
        emailEditText = findViewById<EditText>(R.id.JemailEditText)
        emailTextView = findViewById<TextView>(R.id.emailTextView)
        nextButton2 = findViewById<Button>(R.id.JnextButton2)

        loadData()

        overlapButton2.setOnClickListener{
            sqlitedb = dbManager.readableDatabase
            var input_phone = phoneEditText.text.toString()

            try{
                if (sqlitedb != null){
                    var cursor : Cursor
                    cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE phone = '${input_phone}';", null)
                    cursor.moveToFirst()

                    if (cursor.getCount() != 1){
                        Toast.makeText(this, "사용할 수 있는 전화번호입니다.", Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this, "이미 가입한 전화번호입니다.", Toast.LENGTH_SHORT).show()
                        phoneEditText.setTextColor(ContextCompat.getColor(this@SignUp2, R.color.error))
                    }
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
            }
        }

        nextButton2.setOnClickListener {
            if (phoneEditText == null){
                Toast.makeText(this, "전화번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (birthEditText == null){
                Toast.makeText(this, "생년월일을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (emailEditText == null){
                Toast.makeText(this, "메일을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                saveData(phoneEditText.text.toString(),
                    birthEditText.text.toString(),
                    emailEditText.text.toString())

                val intent = Intent(this, SignUp3::class.java)
                startActivity(intent)
            }
        }
    }

    private fun saveData(phone: String, birth: String, email: String){
        var pref = this.getSharedPreferences("join", 0)
        var editor = pref.edit()

        editor.putString("JOIN_PHONE", phoneEditText.text.toString()).apply()
        editor.putString("JOIN_BIRTH", birthEditText.text.toString()).apply()
        editor.putString("JOIN_EMAIL", emailEditText.text.toString()).apply()
        editor.commit()
    }

    private fun loadData(){
        var pref = this.getSharedPreferences("join", 0)
        var phone = pref.getString("JOIN_PHONE", "")
        var birth = pref.getString("JOIN_BIRTH", "")
        var email = pref.getString("JOIN_EMAIL", "")

        if (phone != "" && birth != "" && email != ""){
            phoneEditText.setText(phone.toString())
            birthEditText.setText(birth.toString())
            emailEditText.setText(email.toString())
        }
    }
}