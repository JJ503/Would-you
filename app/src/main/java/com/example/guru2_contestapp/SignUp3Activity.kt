package com.example.guru2_contestapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

//        jobSpinner.onItemSelectedListener {
//            if (jobSpinner.selectedItem.toString() == "대학생") {
//                univerNameEditText.visibility = View.VISIBLE
//            } else {
//                univerNameEditText.visibility = View.GONE
//            }
//        }


        signUpButton.setOnClickListener {
            if (jobSpinner.selectedItem.toString() == "직업을 선택해주세요") {
                Toast.makeText(this, "직업을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (areaSpinner.selectedItem.toString() == "지역을 선택해주세요") {
                Toast.makeText(this, "지역를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else if (areaSpinner.selectedItem.toString() == "관심사를 선택해주세요") {
                Toast.makeText(this, "관심사를 선택해 주세요.", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this, "성공!!", Toast.LENGTH_SHORT).show()

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




//        loadData()
//        signUpButton.setOnClickListener {
//            saveData(jobSpinner.selectedItem.toString(),
//                    univerNameEditText.text.toString(),
//                    areaSpinner.selectedItem.toString(),
//                    interestSpinner.selectedItem.toString())
//
//            val intent = Intent(this, SignUp3::class.java)
//
//            intent.putExtra("job", jobSpinner.selectedItem.toString())
//            intent.putExtra("univer", univerNameEditText.text.toString())
//            intent.putExtra("area", areaSpinner.selectedItem.toString())
//            intent.putExtra("interest", interestSpinner.selectedItem.toString())
//
//            startActivity(intent)
//        }
    }

    private fun saveData(job: String, univer: String, area: String, interest: String){
        var pref = this.getPreferences(0)
        var editor = pref.edit()

        editor.putString("KEY_JOB", jobSpinner.selectedItem.toString()).apply()
        editor.putString("KEY_UNIVER", univerNameEditText.text.toString()).apply()
        editor.putString("KEY_AREA", areaSpinner.selectedItem.toString()).apply()
        editor.putString("KEY_INTEREST", interestSpinner.selectedItem.toString()).apply()
    }

    private fun loadData(){
        var pref = this.getPreferences(0)
        var job = pref.getString("KEY_JOB", "")
        var univer = pref.getString("KEY_UNIVER", "")
        var area = pref.getString("KEY_AREA", "")
        var interest = pref.getString("KEY_INTEREST", "")


    }
}