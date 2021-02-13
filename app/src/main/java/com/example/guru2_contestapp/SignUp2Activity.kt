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

// 회원가입 두 번째 페이지
class SignUp2Activity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var signUp2 : ConstraintLayout
    lateinit var phoneEditText : EditText
    lateinit var overlapButton2 : Button
    lateinit var birthEditText : EditText
    lateinit var emailEditText : EditText
    lateinit var emailSpinner : Spinner
    lateinit var nextButton2 : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        // 액션바 설정
        supportActionBar?.elevation = 3f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow2)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_signUp)+"</font>"))

        signUp2 = findViewById(R.id.JsingUp2)
        phoneEditText = findViewById<EditText>(R.id.JphoneEditTextText)
        overlapButton2 = findViewById<Button>(R.id.JoverlapButton2)
        birthEditText = findViewById<EditText>(R.id.JbirthEditText)
        emailEditText = findViewById<EditText>(R.id.JemailEditText)
        emailSpinner = findViewById<Spinner>(R.id.JemailSpinner)
        nextButton2 = findViewById<Button>(R.id.JnextButton2)

        // editText 외 다른 부분을 터치하면 키보드 자동 숨김
        signUp2.setOnClickListener {
            CloseKeyboard()
        }

        // DB 연결
        dbManager = DBManager(this, "ContestAppDB", null, 1)

        loadData()  // 저장된 데이터가 있다면 가져오기

        // 틀려서 텍스트 색이 빨간색이 되었을 때 다시 입력을 시작하면 검정으로 바뀜
        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                birthEditText.setTextColor(ContextCompat.getColor(this@SignUp2Activity, R.color.normal))
            }
        })

        // 전화번호 중복 체크
        var overlap2 = false
        overlapButton2.setOnClickListener{
            sqlitedb = dbManager.readableDatabase
            var input_phone = phoneEditText.text.toString()

            try{
                if (sqlitedb != null){
                    var cursor : Cursor
                    cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_tel = '${input_phone}';", null)
                    cursor.moveToFirst()

                    if (phoneEditText.text.toString().length < 11){
                        Toast.makeText(this, "전화번호를 확인해 주세요", Toast.LENGTH_SHORT).show()
                        phoneEditText.setTextColor(ContextCompat.getColor(this@SignUp2Activity, R.color.error))
                    } else {
                        if (cursor.getCount() != 1){
                            overlap2 = true
                            Toast.makeText(this, "사용할 수 있는 전화번호입니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "이미 가입한 전화번호입니다.", Toast.LENGTH_SHORT).show()
                            phoneEditText.setTextColor(ContextCompat.getColor(this@SignUp2Activity, R.color.error))
                        }
                    }
                }
            } catch (e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
            }
        }

        // 틀려서 텍스트 색이 빨간색이 되었을 때 다시 입력을 시작하면 검정으로 바뀜
        birthEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                birthEditText.setTextColor(ContextCompat.getColor(this@SignUp2Activity, R.color.normal))
            }
        })

        // 다음 버튼을 누르면 위에서 입력한 정보들이 모두 저장되어 다음 페이지로 넘어감
        // 만약 하나라도 입력하지 않은 정보가 있다면 넘어갈 수 없음
        nextButton2.setOnClickListener {
            if (phoneEditText.text.toString() == ""){
                Toast.makeText(this, "전화번호를 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (birthEditText.text.toString() == ""){
                Toast.makeText(this, "생년월일을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if (emailEditText.text.toString().trim() == ""){
                Toast.makeText(this, "메일을 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if(emailSpinner.selectedItem.toString() == "선택해주세요") {
                Toast.makeText(this, "메일의 도메인 주소를 선택해 주세요", Toast.LENGTH_SHORT).show()
            } else if(overlap2 == false) {
                Toast.makeText(this, "중복 확인 버튼을 눌러 주세요", Toast.LENGTH_SHORT).show()
            } else if (birthEditText.text.toString().length < 6){
                Toast.makeText(this, " 생년월일을 확인해 주세요", Toast.LENGTH_SHORT).show()
                birthEditText.setTextColor(ContextCompat.getColor(this@SignUp2Activity, R.color.error))
            } else {
                saveData(phoneEditText.text.toString(),
                        birthEditText.text.toString(),
                        emailEditText.text.toString(),
                        emailSpinner.selectedItem.toString())

                val intent = Intent(this, SignUp3Activity::class.java)
                startActivity(intent)
            }
        }


        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                phoneEditText.setTextColor(ContextCompat.getColor(this@SignUp2Activity, R.color.normal))
            }
        })
    }

    private fun saveData(phone: String, birth: String, email: String, domain: String){
        var pref = this.getSharedPreferences("join", 0)
        var editor = pref.edit()

        editor.putString("JOIN_PHONE", phone).apply()
        editor.putString("JOIN_BIRTH", birth).apply()
        editor.putString("JOIN_EMAIL", email).apply()
        editor.putString("JOIN_DOMAIN", domain).apply()
        editor.commit()
    }

    private fun loadData(){
        var pref = this.getSharedPreferences("join", 0)
        var phone = pref.getString("JOIN_PHONE", "")
        var birth = pref.getString("JOIN_BIRTH", "")
        var email = pref.getString("JOIN_EMAIL", "")
        var domain = pref.getString("JOIN_DOMAIN", "")

        if (phone != "" && birth != "" && email != ""){
            phoneEditText.setText(phone.toString())
            birthEditText.setText(birth.toString())
            emailEditText.setText(email.toString())
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