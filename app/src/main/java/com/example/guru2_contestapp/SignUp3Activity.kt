package com.example.guru2_contestapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout

class SignUp3Activity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var jobSpinner: Spinner
    lateinit var univerNameEditText: EditText
    lateinit var areaSpinner: Spinner
    lateinit var interestSpinner: Spinner
    lateinit var signUpButton: Button
    lateinit var checkBox: CheckBox
    lateinit var checkBox2: CheckBox
    lateinit var infoPolicy : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up3)

        supportActionBar?.elevation = 3f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_signUp)+"</font>"))

        jobSpinner = findViewById<Spinner>(R.id.JjobSpinner)
        univerNameEditText = findViewById<EditText>(R.id.JuniverNameEditText)
        areaSpinner = findViewById<Spinner>(R.id.JareaSpinner)
        interestSpinner = findViewById<Spinner>(R.id.JinterestsSpinner)
        signUpButton = findViewById<Button>(R.id.JsignUpButton)
        checkBox = findViewById<CheckBox>(R.id.JcheckBox)
        checkBox2 = findViewById<CheckBox>(R.id.JcheckBox2)
        infoPolicy = findViewById<TextView>(R.id.JinfoPolicy)


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

        infoPolicy.setOnClickListener{
            var builder = AlertDialog.Builder(this)
            builder.setTitle("아이디 찾기")
            builder.setIcon(R.drawable.logo_2_04)
            var message = "1. 개인 정보 처리 방침\n" +
                    "개인 정보 처리 방침은 회사가 서비스를 제공함에 있어, 개인 정보를 어떻게 수집/이용/보관/파기하는지에 대한 정보를 담은 방침을 의미합니다. 개인 정보 처리 방침은 개인정보보호법, 정보통신망 이용 촉진 및 정보보호 등에 관한 법률 등 국내 개인 정보 보호 법령을 모두 준수하고 있습니다. 이 약관의 정의는 서비스 이용약관을 따릅니다.\n" +
                    "\n" +
                    "2. 수집하는 개인 정보의 항목\n" +
                    "회사는 서비스 제공을 위해 아래 항목 중 최소한의 개인 정보를 수집합니다.\n" +
                    "- 이름, 아이디, 이메일, 휴대전화 번호, 생년월일, 활동 지역, 관심사\n" +
                    "* 추가적으로 수집이 필요한 개인 정보 및 개인 정보를 포함한 자료는 이용자 응대 과정과 서비스 내부 알림 수단 등을 통해 별도로 요청/수집될 수 있습니다.\n" +
                    "* 서비스 이용 과정에서 기기 정보, 이용 기록, 로그 기록이 자동으로 수집될 수 있습니다.\n" +
                    "\n" +
                    "3. 수집한 개인 정보의 이용\n" +
                    "회사는 쾌적한 서비스를 제공하기 위해, 아래의 목적에 한해 개인 정보를 이용합니다.\n" +
                    "1) 가입 및 탈퇴 의사 확인, 회원 식별 등의 회원 관리\n" +
                    "2) 서비스 제공 및 기존/신규 시스템 개발/유지/개선\n" +
                    "3) 불법/약관 위반 게시물을 방지하기 위한 운영 시스템 개발/유지/개선\n" +
                    "4) 자료 분석을 통한 맞춤형 콘텐츠 제공\n" +
                    "\n" +
                    "4. 정보주체의 권리, 의무 및 행사\n" +
                    "회원은 언제든지 개인 정보 설정과 회원 탈퇴를 통해 자신의 개인 정보를 조회, 수정, 삭제, 탈퇴할 수 있습니다."
            builder.setMessage(message)
            builder.setPositiveButton("확인", null)
            builder.show()
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
            } else if (!checkBox.isChecked()){
                Toast.makeText(this, "개인정보 보호 동의에 체크해주세요.", Toast.LENGTH_SHORT).show()
            } else if (!checkBox2.isChecked()){
                Toast.makeText(this, "앱/이메일 수신 동의에 체크해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                var pref = this.getSharedPreferences("join", 0)
                var editor = pref.edit()

                var name = pref.getString("JOIN_NAME", "")
                var id = pref.getString("JOIN_ID", "")
                var pw = pref.getString("JOIN_PASSWORD", "")
                var profile = pref.getString("JOIN_PROFILE", "")
                var phone = pref.getString("JOIN_PHONE", "")
                var birth = pref.getString("JOIN_BIRTH", "").toString()
                var year = birth.substring(0, 2)
                var month = birth.substring(2, 4)
                var date = birth.substring(4, 6)
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
                        sqlitedb.execSQL("INSERT INTO member VALUES ('${name}', '${id}', '${pw}', ${profile}, '${phone}', '${year}', '${month}', '${date}', '${user_email}', '${job}', '${univer}', '${area}', '${interest}');")
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