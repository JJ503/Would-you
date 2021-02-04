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

        // 상단 텍스트 뷰(공모전과 팀 이름)
        val intent=intent
        var ic_name=intent.getStringExtra("intent_c_name")
        var it_name=intent.getStringExtra("intent_t_name")
        info.text=it_name+"("+ic_name+")"


        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        cursor=sqlitedb.rawQuery("SELECT t_num FROM team WHERE t_name = '"+it_name+"';", null)
        if(cursor.moveToNext()){
            t_num=cursor.getInt(cursor.getColumnIndex("t_num"))
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

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