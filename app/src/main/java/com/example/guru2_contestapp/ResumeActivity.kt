package com.example.guru2_contestapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout

class ResumeActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var info: TextView
    lateinit var submitBtn: Button
    lateinit var editBtn: ImageButton
    lateinit var hopeET: EditText
    lateinit var selfIntroET: EditText
    lateinit var etcET: EditText
    lateinit var nameTextView: TextView
    lateinit var ageTextView: TextView
    lateinit var jobTextView: TextView
    lateinit var profileImg: ImageView
    lateinit var layout: ConstraintLayout

    lateinit var str_hope: String
    lateinit var str_self_intro: String
    lateinit var str_etc: String
    lateinit var str_name: String
    lateinit var str_year: String
    lateinit var str_job: String
    lateinit var pofile_src: String
    var t_num=0

    override fun onCreate(savedInstanceState: Bundle?) {

        // 로그인한 계정 아이디
        val sharedPreferences : SharedPreferences = this.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume)

        // 바탕 클릭하면 키보드 숨김
        layout=findViewById(R.id.Wlayout)
        layout.setOnClickListener { CloseKeyboard() }

        // 액션바 설정
        supportActionBar?.elevation = 3f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow2)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_resume)+"</font>")

        info=findViewById(R.id.WresumeInfoTextView)
        submitBtn=findViewById(R.id.WsubmitButton)
        hopeET=findViewById<EditText>(R.id.WwishPartEditText)
        selfIntroET=findViewById<EditText>(R.id.WselfIntroEditText)
        etcET=findViewById<EditText>(R.id.WetcEditText)
        nameTextView=findViewById(R.id.WnameTextView)
        ageTextView=findViewById(R.id.WageTextView)
        jobTextView=findViewById(R.id.WjobTextView)
        editBtn=findViewById(R.id.WprofileEditButton)
        profileImg=findViewById(R.id.WimageView)
        CloseKeyboard()


        // 상단 텍스트 뷰(공모전과 팀 이름) 내용을 이전 페이지에서 온 intent 값으로 설정
        val ic_name=intent.getStringExtra("intent_c_name")
        val it_name=intent.getStringExtra("intent_t_name")
        info.text=it_name+"("+ic_name+")"

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        try {
            if(sqlitedb!=null){
                // DB에서 팀 이름 가지고 팀 번호를 찾아 t_num에 저장
                cursor=sqlitedb.rawQuery("SELECT t_num FROM team WHERE t_name = '"+it_name+"';", null)
                if(cursor.count!=0){
                    if(cursor.moveToNext()){
                        t_num=cursor.getInt(cursor.getColumnIndex("t_num"))
                    }
                }
                // DB에서 이름, 나이, 직업, 프로필 사진을 가져옴
                cursor=sqlitedb.rawQuery("SELECT m_name, m_year, m_job, m_profile FROM member WHERE m_id = '"+USER_ID+"';", null)
                if(cursor.count!=0){
                    if(cursor.moveToNext()){
                        str_name=cursor.getString(cursor.getColumnIndex("m_name"))
                        str_year=cursor.getString(cursor.getColumnIndex("m_year"))
                        str_job=cursor.getString(cursor.getColumnIndex("m_job"))
                        pofile_src=cursor.getString(cursor.getColumnIndex("m_profile"))
                    }
                    cursor.close()
                }
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }

        // DB에서 생년을 가져와 나이 계산
        val this_year = Calendar.getInstance().get(Calendar.YEAR)
        val this_year_last = this_year.toString().substring(2, 4)

        var birth_year = 0
        if (str_year.toInt() > this_year_last.toInt()){
            birth_year = ("19" + str_year).toInt()
        } else{
            birth_year = ("20" + str_year).toInt()
        }
        val age = this_year - birth_year + 1

        nameTextView.text=str_name
        ageTextView.text=age.toString()
        jobTextView.text=str_job
        var profile_src_int=this.resources.getIdentifier(pofile_src,"drawable", "com.example.guru2_contestapp")
        profileImg.setImageResource(profile_src_int)

        // 프로필에서 수정 버튼 클릭 -> 수정 페이지로 이동
        editBtn.setOnClickListener {
            val intent= Intent(this, SettingActivity::class.java)
            startActivity(intent)

        }

        // 제출 버튼 클릭하면 입력 폼에 빈칸이 있는지 확인하고 있는 경우 대화상자로 알림
        // 빈칸 없는 경우 입력한 정보를 DB에 값을 입력하고 액티비티 종료
        submitBtn.setOnClickListener {
            val builder= AlertDialog.Builder(this)
            CloseKeyboard()
            if(hopeET.text.toString()==""){
                builder.setMessage("\t\t희망 분야를 입력해 주세요.")
                builder.setTitle(" ")
                builder.setIcon(R.drawable.logo_2_04)
                builder.create()
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if (selfIntroET.text.toString()==""){
                builder.setMessage("\t\t자기소개를 해 주세요.")
                builder.setTitle(" ")
                builder.setIcon(R.drawable.logo_2_04)
                builder.create()
                builder.setPositiveButton("확인", null)
                builder.show()
            } else{
                str_hope=hopeET.text.toString()
                str_self_intro=selfIntroET.text.toString()
                str_etc=etcET.text.toString()

                dbManager = DBManager(this, "ContestAppDB", null, 1)
                sqlitedb = dbManager.writableDatabase
                try {
                    if(sqlitedb!=null){
                        sqlitedb.execSQL("INSERT INTO resume (m_id, t_num, r_hope, r_self_intro, r_etc) VALUES ('"+USER_ID+"', "+t_num+", '"+str_hope+"', '"+str_self_intro+"', '"+str_etc+"')")
                        sqlitedb.execSQL("INSERT INTO teamManage (m_id, t_num, state) VALUES ('"+USER_ID+"', "+t_num+", 0)")
                    }
                } catch(e: Exception){
                    Log.e("Error", e.message.toString())
                } finally{
                    sqlitedb.close()
                    dbManager.close()
                }
                this.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.writableDatabase
        var cursor: Cursor
        try {
            if(sqlitedb!=null){
                cursor=sqlitedb.rawQuery("SELECT m_job FROM member WHERE m_name = '"+str_name+"';", null)
                if(cursor.count!=0){
                    if(cursor.moveToNext()){
                        str_job=cursor.getString(cursor.getColumnIndex("m_job"))
                    }
                    cursor.close()
                }
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }
        jobTextView.text=str_job
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun CloseKeyboard() {
        var view = this.currentFocus

        if(view != null)
        {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}