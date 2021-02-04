package com.example.guru2_contestapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class ResumeActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var info: TextView
    lateinit var submitBtn: Button
    lateinit var hopeET: EditText
    lateinit var selfIntroET: EditText
    lateinit var etcET: EditText

    lateinit var str_id: String
    lateinit var str_hope: String
    lateinit var str_self_intro: String
    lateinit var str_etc: String
    var t_num=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume)

        info=findViewById(R.id.WresumeInfoTextView)
        submitBtn=findViewById(R.id.WsubmitButton)
        hopeET=findViewById<EditText>(R.id.WwishPartEditText)
        selfIntroET=findViewById<EditText>(R.id.WselfIntroEditText)
        etcET=findViewById<EditText>(R.id.WetcEditText)

        // 상단 텍스트 뷰(공모전과 팀 이름) 내용을 이전 페이지에서 온 intent 값으로 설정
        val intent=intent
        val ic_name=intent.getStringExtra("intent_c_name")
        val it_name=intent.getStringExtra("intent_t_name")
        info.text=it_name+"("+ic_name+")"

        // DB에서 팀 이름 가지고 팀 번호를 찾아 t_num에 저장
        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        val cursor: Cursor
        cursor=sqlitedb.rawQuery("SELECT t_num FROM team WHERE t_name = '"+it_name+"';", null)
        if(cursor.moveToNext()){
            t_num=cursor.getInt(cursor.getColumnIndex("t_num"))
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 제출 버튼 클릭하면 입력 폼에 빈칸이 있는지 확인하고 있는 경우 대화상자로 알림
        // 빈칸 없는 경우 입력한 정보를 DB에 값을 입력하고 액티비티 종료
        submitBtn.setOnClickListener {
            val builder= AlertDialog.Builder(this)

            if(hopeET.text.toString()==""){
                builder.setMessage("희망 분야를 입력해 주세요.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if (selfIntroET.text.toString()==""){
                builder.setMessage("자기소개를 해 주세요.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else{
                str_id="wow later please"
                str_hope=hopeET.text.toString()
                str_self_intro=selfIntroET.text.toString()
                str_etc=etcET.text.toString()

                dbManager = DBManager(this, "ContestAppDB", null, 1)
                sqlitedb = dbManager.writableDatabase
                sqlitedb.execSQL("INSERT INTO resume (s_id, t_num, r_hope, r_self_intro, r_etc) VALUES('"+str_id+"', "+t_num+", '"+str_hope+"', '"+str_self_intro+"', '"+str_etc+"')")

                sqlitedb.close()
                dbManager.close()

                this.finish()
            }
        }
    }
}