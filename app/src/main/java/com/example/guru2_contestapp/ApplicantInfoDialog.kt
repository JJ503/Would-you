package com.example.guru2_contestapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.*

class ApplicantInfoDialog(context : Context, val m_id : String, val t_num : Int) {  // a_id : 신청자 id
    val dlg = Dialog(context)

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var btnClose : ImageView
    lateinit var infoName : TextView
    lateinit var infoAge : TextView
    lateinit var infoHope : TextView
    lateinit var infoTel : TextView
    lateinit var infoEmail : TextView
    lateinit var infoJob : TextView
    lateinit var infoArea : TextView
    lateinit var infoInterest : TextView
    lateinit var infoIntro : TextView
    lateinit var infoEtc : TextView


    @SuppressLint("ResourceAsColor")
    fun infoDlg() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)      // 타이틀바 제거
        dlg.setContentView(R.layout.applicant_info_dialog)     // 다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(true)                                // 다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함

        btnClose = dlg.findViewById(R.id.JbtnClose)
        btnClose.setOnClickListener {
            dlg.dismiss()
        }

        dbManager = DBManager(dlg.context, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        infoName = dlg.findViewById(R.id.JinfoName)
        infoAge = dlg.findViewById(R.id.JinfoAge)
        infoHope = dlg.findViewById(R.id.JinfoHope)
        infoTel = dlg.findViewById(R.id.JinfoTel)
        infoEmail = dlg.findViewById(R.id.JinfoEmail)
        infoJob = dlg.findViewById(R.id.JinfoJob)
        infoArea = dlg.findViewById(R.id.JinfoArea)
        infoInterest = dlg.findViewById(R.id.JinfoInterest)
        infoIntro = dlg.findViewById(R.id.JinfoIntro)
        infoEtc = dlg.findViewById(R.id.JinfoEtc)

        // 신청자의 지원서와 정보 가져오기
        try {
            var m_cursor : Cursor
            m_cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '${m_id}';", null)
            m_cursor.moveToFirst()

            var r_cursor : Cursor
            r_cursor = sqlitedb.rawQuery("SELECT * FROM resume WHERE m_id = '${m_id}' AND t_num = ${t_num};", null)
            r_cursor.moveToFirst()

            var tm_cursor : Cursor
            tm_cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '${m_id}' AND t_num = ${t_num};", null)
            tm_cursor.moveToFirst()

            if (m_cursor.getCount() == 1 && r_cursor.getCount() == 1 && tm_cursor.getCount() == 1){
                infoName.text = m_cursor.getString(m_cursor.getColumnIndex("m_name")).toString()

                var m_age = calcAge(m_cursor.getString(m_cursor.getColumnIndex("m_year")).toString())
                infoAge.text = m_age.toString()

                infoHope.text = r_cursor.getString(r_cursor.getColumnIndex("r_hope")).toString()
                infoJob.text = m_cursor.getString(m_cursor.getColumnIndex("m_job")).toString()
                infoInterest.text = m_cursor.getString(m_cursor.getColumnIndex("m_interest")).toString()
                infoIntro.text = r_cursor.getString(r_cursor.getColumnIndex("r_self_intro")).toString()
                infoEtc.text = r_cursor.getString(r_cursor.getColumnIndex("r_etc")).toString()

                // 수락후 확인 가능한 정보들 처리
                if (tm_cursor.getInt(tm_cursor.getColumnIndex("state")) == 1 || tm_cursor.getInt(tm_cursor.getColumnIndex("state")) == 5){
                    infoTel.text = m_cursor.getString(m_cursor.getColumnIndex("m_tel")).toString()
                    infoTel.setTextColor(Color.parseColor("#302A2A"))

                    infoEmail.text = m_cursor.getString(m_cursor.getColumnIndex("m_email")).toString()
                    infoEmail.setTextColor(Color.parseColor("#302A2A"))

                    infoArea.text = m_cursor.getString(m_cursor.getColumnIndex("m_area")).toString()
                    infoArea.setTextColor(Color.parseColor("#302A2A"))
                } else {
                    infoTel.text = "수락 후 확인 가능합니다"
                    infoTel.setTextColor(Color.parseColor("#ff5151"))

                    infoEmail.text = "수락 후 확인 가능합니다"
                    infoEmail.setTextColor(Color.parseColor("#ff5151"))

                    infoArea.text = "수락 후 확인 가능합니다"
                    infoArea.setTextColor(Color.parseColor("#ff5151"))
                }

            } else {
                Toast.makeText(dlg.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }

        dlg.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)
        dlg.show()
    }


    // 나이 구하기
    private fun calcAge(m_year: String): Int {

        val this_year = Calendar.getInstance().get(Calendar.YEAR)
        val this_year_last = this_year.toString().substring(2, 4)

        var birth_year = 0
        if (m_year.toInt() > this_year_last.toInt()){
            birth_year = ("19" + m_year).toInt()
        } else{
            birth_year = ("20" + m_year).toInt()
        }

        birth_year = this_year - birth_year + 1

        return birth_year
    }
}