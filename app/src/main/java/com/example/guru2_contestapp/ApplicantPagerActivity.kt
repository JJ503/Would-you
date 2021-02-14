package com.example.guru2_contestapp

import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
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

        // 액션바 설정
        supportActionBar?.elevation = 3f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_left_arrow2)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_applicantPager)+"</font>"))

        // 페이지 리스트 배열
        pagerArray = ArrayList()

        applicantPager = findViewById(R.id.applicantPager)
        applicantPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        btnRight = findViewById(R.id.btnRight)
        btnLeft = findViewById(R.id.btnLeft)
        personNumTv = findViewById(R.id.personNumTv)

        // DB 연동
        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        // 현재 팀 번호와 팀 상태의 키 값을 불러옴
        val sharedPreferences: SharedPreferences = getSharedPreferences("t_num", AppCompatActivity.MODE_PRIVATE)
        val t_num = sharedPreferences.getInt("t_num", -1)
        var t_endStatus = sharedPreferences.getInt("t_endStatus", -1)  //모집 종료면 0, 모집 중이면 1

        // 신청자 정보 리스트 (pager 버전)
        var cursor : Cursor? = null
        try {
            var t_cursor : Cursor
            t_cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = ${t_num}", null)
            t_cursor.moveToFirst()
            val t_complete :Int = t_cursor.getInt(t_cursor.getColumnIndex("t_complete"))  // 완료 전인 팀은 0, 완료된 팀은 1

            // 완료된 팀 : 팀원만 보임
            if (t_complete == 1){
                cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${t_num} AND state = 5", null)

                while (cursor.moveToNext()) {
                    t_endStatus = 5

                    var m_id = cursor.getString(cursor.getColumnIndex("m_id")).toString()

                    var m_cursor: Cursor
                    m_cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '${m_id}'", null)
                    m_cursor.moveToFirst()

                    var r_cursor: Cursor
                    r_cursor = sqlitedb.rawQuery("SELECT * FROM resume WHERE m_id = '${m_id}' AND t_num = ${t_num}", null)
                    r_cursor.moveToFirst()

                    var m_profile = m_cursor.getString(m_cursor.getColumnIndex("m_profile"))
                    var m_name = m_cursor.getString(m_cursor.getColumnIndex("m_name")).toString()
                    var m_year = m_cursor.getString(m_cursor.getColumnIndex("m_year")).toString()
                    var m_age = calcAge(m_year)

                    var r_hope = r_cursor.getString(r_cursor.getColumnIndex("r_hope")).toString()

                    pagerArray.add(ApplicantPagerItem(t_num, t_endStatus, m_id, m_name, m_age, r_hope, m_profile))
                }
            } else {   // 모집 중인 팀 : 수락, 거절, 신청 상태 모두 보임
                // 수락(1), 신청(0), 거절(-1) 상태 순서대로 나오도록 state의 역순으로 출력
                cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${t_num} AND state != 2 ORDER BY state DESC", null)

                while (cursor.moveToNext()) {
                    var m_id = cursor.getString(cursor.getColumnIndex("m_id")).toString()

                    var m_cursor: Cursor
                    m_cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '${m_id}'", null)
                    m_cursor.moveToFirst()

                    var r_cursor: Cursor
                    r_cursor = sqlitedb.rawQuery("SELECT * FROM resume WHERE m_id = '${m_id}' AND t_num = ${t_num}", null)
                    r_cursor.moveToFirst()

                    var m_profile = m_cursor.getString(m_cursor.getColumnIndex("m_profile"))
                    var m_name = m_cursor.getString(m_cursor.getColumnIndex("m_name")).toString()
                    var m_year = m_cursor.getString(m_cursor.getColumnIndex("m_year")).toString()
                    var m_age = calcAge(m_year)

                    var r_hope = r_cursor.getString(r_cursor.getColumnIndex("r_hope")).toString()

                    pagerArray.add(ApplicantPagerItem(t_num, t_endStatus, m_id, m_name, m_age, r_hope, m_profile))
                }
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
        }

        // 리사이클러 뷰에 어댑터 설정
        applicantPager.adapter = ApplicantPagerAdapter(pagerArray)

        // 상단에 몇 명 중 몇 번 째 신청자 정보 페이지인지 보여줌
        var current = applicantPager.currentItem
        var person_num = "1 / " + cursor?.getCount()
        personNumTv.setText(person_num)


        // position이 전달되면 해당 사람부터 보이도록 설정
        if (intent.hasExtra("pos")) {
            current = intent.getIntExtra("pos", -1)
            applicantPager.setCurrentItem(current, false)
            person_num = (current + 1).toString() + " / " + cursor?.getCount()
            personNumTv.setText(person_num)
        } else {
            Toast.makeText(this, "전달된 값이 없습니다", Toast.LENGTH_SHORT).show()
        }


        // 오른쪽 버튼을 누른 경우 오른쪽 페이지로 이동
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

        // 왼쪽 버튼을 누른 경우 왼쪽 페이지로 이동
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
