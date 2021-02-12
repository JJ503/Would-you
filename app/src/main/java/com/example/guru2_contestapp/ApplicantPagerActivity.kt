package com.example.guru2_contestapp

import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import java.util.*
import kotlin.collections.ArrayList

class ApplicantPagerActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var applicantPager: ViewPager2
    lateinit var btnRight: ImageButton
    lateinit var btnLeft: ImageButton
    lateinit var personNumTv: TextView

    lateinit var pagerArray: ArrayList<ApplicantPagerItem>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_pager)

        pagerArray = ArrayList()

        applicantPager = findViewById(R.id.applicantPager)
        applicantPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        btnRight = findViewById(R.id.btnRight)
        btnLeft = findViewById(R.id.btnLeft)
        personNumTv = findViewById(R.id.personNumTv)

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        val sharedPreferences: SharedPreferences = getSharedPreferences("t_num", AppCompatActivity.MODE_PRIVATE)
        val t_num = sharedPreferences.getInt("t_num", -1)
        val t_endStatus = sharedPreferences.getInt("t_endStatus", -1)  //모집 종료면 0, 모집 중이면 1

        var cursor : Cursor? = null
        try {
            cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${t_num} AND state != 2 ORDER BY state DESC", null)

            while (cursor.moveToNext()){
                var m_id = cursor.getString(cursor.getColumnIndex("m_id")).toString()

                var m_cursor : Cursor
                m_cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '${m_id}'", null)
                m_cursor.moveToFirst()

                var r_cursor : Cursor
                r_cursor = sqlitedb.rawQuery("SELECT * FROM resume WHERE m_id = '${m_id}' AND t_num = ${t_num}", null)
                r_cursor.moveToFirst()

                var m_name = m_cursor.getString(m_cursor.getColumnIndex("m_name")).toString()
                var m_year = m_cursor.getString(m_cursor.getColumnIndex("m_year")).toString()
                var m_age = calcAge(m_year)

                var r_hope = r_cursor.getString(r_cursor.getColumnIndex("r_hope")).toString()
                var m_tel = m_cursor.getString(m_cursor.getColumnIndex("m_tel")).toString()
                var m_email = m_cursor.getString(m_cursor.getColumnIndex("m_email")).toString()
                var m_job = m_cursor.getString(m_cursor.getColumnIndex("m_job")).toString()
                var m_area = m_cursor.getString(m_cursor.getColumnIndex("m_area")).toString()
                var m_interest = m_cursor.getString(m_cursor.getColumnIndex("m_interest")).toString()
                var r_self_intro = r_cursor.getString(r_cursor.getColumnIndex("r_self_intro")).toString()
                var r_etc = r_cursor.getString(r_cursor.getColumnIndex("r_etc")).toString()

                pagerArray.add(ApplicantPagerItem(t_num, t_endStatus, m_id, m_name, m_age, r_hope, m_tel, m_email, m_job, m_area, m_interest, r_self_intro, r_etc))
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
        }

        applicantPager.adapter = ApplicantPagerAdapter(pagerArray)

        var current = applicantPager.currentItem
        var person_num = "1 / " + cursor?.getCount()
        personNumTv.setText(person_num)


        if (intent.hasExtra("pos")) {
            current = intent.getIntExtra("pos", -1)
            applicantPager.setCurrentItem(current, false)
            person_num = (current + 1).toString() + " / " + cursor?.getCount()
            personNumTv.setText(person_num)
        } else {
            Toast.makeText(this, "전달된 값이 없습니다", Toast.LENGTH_SHORT).show()
        }


        btnRight.setOnClickListener {
            current++

            if (current == cursor?.getCount()) {
                current--
                Toast.makeText(this, "마지막 페이지 입니다", Toast.LENGTH_SHORT).show()
            } else {
                applicantPager.setCurrentItem(current, false)
                person_num = (current + 1).toString() + " / " + cursor?.getCount()
                personNumTv.setText(person_num)
            }
        }

        btnLeft.setOnClickListener {
            current--

            if (current == -1) {
                current++
                Toast.makeText(this, "첫 페이지 입니다", Toast.LENGTH_SHORT).show()

            } else {
                applicantPager.setCurrentItem(current, false)
                person_num = (current + 1).toString() + " / " + cursor?.getCount()
                personNumTv.setText(person_num)
            }

        }
    }

    // 나이 구하기
    private fun calcAge(m_year: String): Int {

        val this_year = Calendar.getInstance().get(Calendar.YEAR)

        var birth_year = 0
        if (m_year.toInt() > this_year){
            birth_year = ("19" + m_year).toInt()
        } else{
            birth_year = ("20" + m_year).toInt()
        }

        birth_year = this_year - birth_year + 1

        return birth_year
    }
}
