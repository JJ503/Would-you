package com.example.guru2_contestapp

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var signUp1 : ConstraintLayout
    lateinit var profileImage : ImageView
    lateinit var profileChangeText : TextView
    lateinit var nameEditText : EditText
    lateinit var idEditText : EditText
    lateinit var overlapButton : Button
    lateinit var passwordEditText : EditText
    lateinit var passCheckEditText : EditText

    lateinit var nextButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.elevation = 3f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow2)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_signUp)+"</font>"))

        signUp1 = findViewById(R.id.JsingUp1)
        profileImage = findViewById(R.id.JprofileImage)
        profileChangeText = findViewById(R.id.JprofileChangeTextView)
        nameEditText = findViewById(R.id.JnameEditText)
        idEditText = findViewById(R.id.JIdEditText)
        overlapButton = findViewById(R.id.JoverlapButton)
        passwordEditText = findViewById(R.id.JpasswordEditText)
        passCheckEditText = findViewById(R.id.JpassCheckEditText)

        nextButton = findViewById<Button>(R.id.JnextButton)

        signUp1.setOnClickListener {
            CloseKeyboard()
        }

        dbManager = DBManager(this, "ContestAppDB", null, 1)

        loadData()

        var profile : String = ""
        if (intent.hasExtra("profile")) {
            profile = intent.getStringExtra("profile").toString()
            Log.d("=== Image Resource ===", profile)
        } else {
            profile = "profile0"
        }

        var profile_src = this.resources.getIdentifier(profile,"drawable", "com.example.guru2_contestapp")
        profileImage.setImageResource(profile_src)

        profileChangeText.setOnClickListener {
            val intent = Intent(this, SetProfileActivity::class.java)
            intent.putExtra("from", "SingUp")
            startActivity(intent)
        }


        var overlap = false
        overlapButton.setOnClickListener {
            sqlitedb = dbManager.readableDatabase
            var input_id = idEditText.text.toString()

            try{
                if (sqlitedb != null){
                    var cursor : Cursor
                    cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '${input_id}';", null)
                    cursor.moveToFirst()

                    if (cursor.getCount() != 1){
                        if (idEditText.text.toString().trim() == ""){
                            Toast.makeText(this, "아이디를 입력해 주세요", Toast.LENGTH_SHORT).show()
                        } else{
                            overlap = true
                            Toast.makeText(this, "사용할 수 있는 아이디입니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else{
                        Toast.makeText(this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show()
                        idEditText.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.error))
                    }
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
            }
        }

        idEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                idEditText.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.normal))
            }
        })

        passCheckEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                passCheckEditText.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.error))
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (passwordEditText.text.toString() != passCheckEditText.text.toString()){
                    passCheckEditText.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.error))
                } else{
                    passCheckEditText.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.normal))
                }
            }
        })

        nextButton.setOnClickListener {
            if (nameEditText.text.toString().trim() == ""){
                Toast.makeText(this, "이름을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (idEditText.text.toString().trim() == ""){
                Toast.makeText(this, "아이디를 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (passwordEditText.text.toString().trim() == ""){
                Toast.makeText(this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (passCheckEditText.text.toString().trim() == ""){
                Toast.makeText(this, "비밀번호 확인을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if(overlap == false) {
                Toast.makeText(this, "중복 확인 버튼을 눌러 주세요", Toast.LENGTH_SHORT).show()
            } else {
                saveData(profile,
                        nameEditText.text.toString(),
                        idEditText.text.toString(),
                        passwordEditText.text.toString())

                val intent = Intent(this, SignUp2Activity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun saveData(profile: String, name: String, id: String, pw: String){
        var pref = this.getSharedPreferences("join", 0)
        var editor = pref.edit()

        editor.putString("JOIN_PROFILE", profile).apply()
        editor.putString("JOIN_NAME", name).apply()
        editor.putString("JOIN_ID", id).apply()
        editor.putString("JOIN_PASSWORD", pw).apply()
        editor.commit()
    }

    private fun loadData(){
        var pref = this.getSharedPreferences("join", 0)

        var profile = pref.getString("JOIN_PROFILE", "")
        var name = pref.getString("JOIN_NAME", "")
        var id = pref.getString("JOIN_ID", "")

        if (profile != "" && name != "" && id != ""){
            var profile_src = this.resources.getIdentifier(profile,"drawable", "com.example.guru2_contestapp")
            profileImage.setImageResource(profile_src)
            nameEditText.setText(name.toString())
            idEditText.setText(id.toString())
        }
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