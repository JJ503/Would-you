package com.example.guru2_contestapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat

class SignUp : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var profileImage: ImageView
    lateinit var profileChangeText: TextView
    lateinit var nameEditText: EditText
    lateinit var idEditText: EditText
    lateinit var overlapButton: Button
    lateinit var passwordEditText: EditText
    lateinit var passCheckEditText: EditText

    lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setTitle("회원가입")

        profileImage = findViewById(R.id.JprofileImage)
        profileChangeText = findViewById(R.id.JprofileChangeTextView)
        nameEditText = findViewById(R.id.JnameEditText)
        idEditText = findViewById(R.id.JIdEditText)
        overlapButton = findViewById(R.id.JoverlapButton)
        passwordEditText = findViewById(R.id.JpasswordEditText)
        passCheckEditText = findViewById(R.id.JpassCheckEditText)

        nextButton = findViewById<Button>(R.id.JnextButton)

        dbManager = DBManager(this, "ContestAppDB", null, 1)

        loadData()

        profileChangeText.setOnClickListener {
            //갤러리 or 사진 앱 실행하여 사진을 선택하도록..
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0)
        }


        overlapButton.setOnClickListener {
            sqlitedb = dbManager.readableDatabase
            var input_id = idEditText.text.toString()

            try {
                if (sqlitedb != null) {
                    var cursor: Cursor
                    cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE id = '${input_id}';", null)
                    cursor.moveToFirst()

                    if (cursor.getCount() != 1) {
                        Toast.makeText(this, "사용할 수 있는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
                        idEditText.setTextColor(ContextCompat.getColor(this@SignUp, R.color.error))
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            } finally {
                sqlitedb.close()
            }
        }

        passCheckEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (passwordEditText.text.toString() != passCheckEditText.text.toString()) {
                    passCheckEditText.setTextColor(ContextCompat.getColor(this@SignUp, R.color.error))
                    passCheckEditText.setBackgroundColor(ContextCompat.getColor(this@SignUp, R.color.error))
                }
            }
        })

        idEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                idEditText.setTextColor(ContextCompat.getColor(this@SignUp, R.color.normal))
            }
        })

        passCheckEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                passCheckEditText.setTextColor(ContextCompat.getColor(this@SignUp, R.color.error))
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (passwordEditText.text.toString() != passCheckEditText.text.toString()) {
                    passCheckEditText.setTextColor(ContextCompat.getColor(this@SignUp, R.color.error))
                } else {
                    passCheckEditText.setTextColor(ContextCompat.getColor(this@SignUp, R.color.normal))
                }
            }
        })

        nextButton.setOnClickListener {
            if (nameEditText == null) {
                Toast.makeText(this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (idEditText == null) {
                Toast.makeText(this, "아이디를 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (passwordEditText == null) {
                Toast.makeText(this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (passCheckEditText == null) {
                Toast.makeText(this, "비밀번호 확인을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                saveData(nameEditText.text.toString(),
                        idEditText.text.toString(),
                        passwordEditText.text.toString())

                val intent = Intent(this, SignUp2::class.java)
                startActivity(intent)
            }

        }
    }

    private fun saveData(name: String, id: String, password: String) {
        var pref = this.getSharedPreferences("join", 0)
        var editor = pref.edit()

        editor.putString("JOIN_NAME", nameEditText.text.toString()).apply()
        editor.putString("JOIN_ID", idEditText.text.toString()).apply()
        editor.putString("JOIN_PASSWORD", passwordEditText.text.toString()).apply()
        editor.commit()
    }

    private fun loadData() {
        var pref = this.getSharedPreferences("join", 0)
        var name = pref.getString("JOIN_NAME", "")
        var id = pref.getString("JOIN_ID", "")
        var password = pref.getString("JOIN_PASSWORD", "")

        if (name != "" && id != "" && password != "") {
            nameEditText.setText(name.toString())
            idEditText.setText(id.toString())
        }
    }
}