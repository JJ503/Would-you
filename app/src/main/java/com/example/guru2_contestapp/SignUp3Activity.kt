package com.example.guru2_contestapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*

class SignUp3Activity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var jobSpinner: Spinner
    lateinit var univerNameEditText: EditText
    lateinit var areaSpinner: Spinner
    lateinit var interestSpinner: Spinner
    lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up3)

        jobSpinner = findViewById<Spinner>(R.id.JjobSpinner)
        univerNameEditText = findViewById<EditText>(R.id.JuniverNameEditText)
        areaSpinner = findViewById<Spinner>(R.id.JareaSpinner)
        interestSpinner = findViewById<Spinner>(R.id.JinterestsSpinner)
        signUpButton = findViewById<Button>(R.id.JsignUpButton)

        dbManager = DBManager(this, "ContestAppDB", null, 1)

        jobSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (jobSpinner.getItemAtPosition(position)){
                    "대학생" -> {
                        univerNameEditText.visibility = VISIBLE
                    }

                    else -> {
                        univerNameEditText.visibility = GONE
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                univerNameEditText.visibility = GONE
            }

        }


        signUpButton.setOnClickListener {
            if (jobSpinner.selectedItem.toString() == "직업을 선택해주세요") {
                Toast.makeText(this, "직업을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (areaSpinner.selectedItem.toString() == "지역을 선택해주세요") {
                Toast.makeText(this, "지역를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (areaSpinner.selectedItem.toString() == "관심사를 선택해주세요") {
                Toast.makeText(this, "관심사를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (jobSpinner.selectedItem.toString() == "대학생" && univerNameEditText.text.toString().trim() == "") {
                Toast.makeText(this, "대학교를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                var pref = this.getSharedPreferences("join", 0)
                var editor = pref.edit()

                var name = pref.getString("JOIN_NAME", "")
                var id = pref.getString("JOIN_ID", "")
                var pw = pref.getString("JOIN_PASSWORD", "")
                var phone = pref.getString("JOIN_PHONE", "")
                var birth = pref.getString("JOIN_BIRTH", "").toString()
                var year = birth.substring(0, 1)
                var month = birth.substring(2, 3)
                var date = birth.substring(4, 5)
                var email = pref.getString("JOIN_EMAIL", "")
                var domain = pref.getString("JOIN_DOMAIN", "")
                var user_email = email + "@" + domain
                var job = jobSpinner.selectedItem.toString()
                var univer = univerNameEditText.text.toString()
                var area = areaSpinner.selectedItem.toString()
                var interest = interestSpinner.selectedItem.toString()

                sqlitedb = dbManager.writableDatabase

                try {
                    if (sqlitedb != null) {
                        sqlitedb.execSQL("INSERT INTO member VALUES ('${name}', '${id}', '${pw}', null, '${phone}', '${year}', '${month}', '${date}', '${user_email}', '${job}', '${univer}', '${area}', '${interest}');")
                        Toast.makeText(this, "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show()

                        editor.remove("JOIN_NAME")
                        editor.remove("JOIN_ID")
                        editor.remove("JOIN_PASSWORD")
                        editor.remove("JOIN_PHONE")
                        editor.remove("JOIN_BIRTH")
                        editor.remove("JOIN_EMAIL")
                        editor.commit()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Log.e("Error", e.message.toString())
                } finally {
                    sqlitedb.close()
                }
            }
        }
    }
}