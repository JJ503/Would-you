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

// 회원가입 첫 페이지
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

        // 액션바 설정
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

        // editText 외 다른 부분을 터치하면 키보드 자동 숨김
        signUp1.setOnClickListener {
            CloseKeyboard()
        }

        // DB 연결
        dbManager = DBManager(this, "ContestAppDB", null, 1)

        loadData()  // 저장된 데이터가 있다면 가져오기

        // 프로필 사진 설정
        var profile : String = ""
        if (intent.hasExtra("profile")) {
            profile = intent.getStringExtra("profile").toString()
            Log.d("=== Image Resource ===", profile)
        } else {
            profile = "profile0"
        }

        // 프로필 사진 imageView에 설정
        var profile_src = this.resources.getIdentifier(profile,"drawable", "com.example.guru2_contestapp")
        profileImage.setImageResource(profile_src)

        // 프로필 변경
        profileChangeText.setOnClickListener {
            val intent = Intent(this, SetProfileActivity::class.java)
            intent.putExtra("from", "SingUp")
            startActivity(intent)
        }

        // ID 중복 체크
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

        // 틀려서 텍스트 색이 빨간색이 되었을 때 다시 입력을 시작하면 검정으로 바뀜
        idEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                idEditText.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.normal))
            }
        })

        // 비밀번호 확인 입력시 실시간으로 비밀번호와 동일한지 확인
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

        // 다음 버튼을 누르면 위에서 입력한 정보들이 모두 저장되어 다음 페이지로 넘어감
        // 만약 하나라도 입력하지 않은 정보가 있다면 넘어갈 수 없음
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

    // 입력한 정보들 저장
    private fun saveData(profile: String, name: String, id: String, pw: String){
        var pref = this.getSharedPreferences("join", 0)
        var editor = pref.edit()

        editor.putString("JOIN_PROFILE", profile).apply()
        editor.putString("JOIN_NAME", name).apply()
        editor.putString("JOIN_ID", id).apply()
        editor.putString("JOIN_PASSWORD", pw).apply()
        editor.commit()
    }

    // 다음 페이지에 갔다가 돌아오면 정보를 그대로 입력해줌
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