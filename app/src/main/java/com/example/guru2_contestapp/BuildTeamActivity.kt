package com.example.guru2_contestapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.util.*
import kotlin.collections.ArrayList

class BuildTeamActivity : AppCompatActivity() {
    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var contestSpinner: Spinner
    lateinit var calenderView: CalendarView
    lateinit var yearTextView: TextView
    lateinit var monthTextView: TextView
    lateinit var dateTextView: TextView
    lateinit var reg_finishBtn: Button
    lateinit var contestNameET: EditText
    lateinit var personNumET: EditText
    lateinit var needPartET: EditText
    lateinit var teamIntroET: EditText

    lateinit var arrayCName: String
    lateinit var contestArray: MutableList<String>

    lateinit var str_c_name: String
    lateinit var str_t_name: String
    lateinit var str_t_total_num: String
    lateinit var str_t_end_date: String
    lateinit var str_t_need_part: String
    lateinit var str_t_detail: String
    var c_num=0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build_team)

        contestSpinner=findViewById<Spinner>(R.id.WcontestSpinner)
        calenderView=findViewById<CalendarView>(R.id.WcalendarView)
        yearTextView=findViewById<TextView>(R.id.WyearTextView)
        monthTextView=findViewById<TextView>(R.id.WmonthTextView)
        dateTextView=findViewById<TextView>(R.id.WdateTextView)
        reg_finishBtn=findViewById<Button>(R.id.WregFinishButton)
        contestNameET=findViewById(R.id.WteamNameEditText)
        personNumET=findViewById(R.id.WpersonNumEditText)
        needPartET=findViewById(R.id.WneedPartEditText)
        teamIntroET=findViewById(R.id.WteamIntroEditText)

        calenderView.visibility= View.GONE

        contestArray=ArrayList<String>()
        contestArray.add("공모전을 선택하세요")

        //공모전 목록 스피너
        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        cursor=sqlitedb.rawQuery("SELECT * FROM contest;", null)

        while(cursor.moveToNext()){
            arrayCName=cursor.getString(cursor.getColumnIndex("c_name")).toString()
            contestArray.add(arrayCName)
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        contestSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contestArray)

        val intent=intent
        var i_name=intent.getStringExtra("intent_c_name")
        var name_index=contestArray.indexOf(i_name)
        contestSpinner.setSelection(name_index)

        contestSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                contestSpinner.setSelection(1)
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
            }
        }

        //마감일 달력
        yearTextView.setOnClickListener {
            calenderView.visibility= View.VISIBLE
        }

        monthTextView.setOnClickListener {
            calenderView.visibility= View.VISIBLE
        }

        dateTextView.setOnClickListener {
            calenderView.visibility= View.VISIBLE
        }

        calenderView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            yearTextView.text= year.toString()
            monthTextView.text= (month+1).toString()
            dateTextView.text= dayOfMonth.toString()
            calenderView.visibility= View.GONE
        }

        // 팀 생성 완료 버튼
        reg_finishBtn.setOnClickListener {
            // 오늘 날짜, 캘린더 선택 날짜 차이 계산
            val today= Calendar.getInstance().apply{
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val year=yearTextView.text.toString()
            val month=monthTextView.text.toString()
            val day=dateTextView.text.toString()
            val deadline= Calendar.getInstance().apply {
                set(Calendar.YEAR, year.toInt())
                set(Calendar.MONTH, (month.toInt())-1)
                set(Calendar.DAY_OF_MONTH, day.toInt())
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val calcDate=(deadline-today) / (24*60*60*1000)

            // 빈칸이 있는 경우 대화상자
            var builder= AlertDialog.Builder(this)

            if(contestSpinner.selectedItem=="공모전을 선택하세요"){
                builder.setMessage("공모전을 선택해주세요.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if (contestNameET.text.toString()==""){
                builder.setMessage("팀 이름을 입력해 주세요.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if(yearTextView.text.toString()=="" || monthTextView.text.toString()=="" || dateTextView.text.toString()==""){
                builder.setMessage("마감일을 입력해 주세요.")
                //builder.setIcon(R.)
                //마감일이 '오늘'보다 앞인 경우 잘못입력했다는 메시지!
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if(personNumET.text.toString()==""){
                builder.setMessage("인원 수를 입력해 주세요.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if(needPartET.text.toString()==""){
                builder.setMessage("모집 분야를 입력해 주세요.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if(teamIntroET.text.toString()==""){
                builder.setMessage("팀 소개를 입력해 주세요.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if(calcDate.toInt()<=0){
                builder.setMessage("마감일은 팀 생성일로부터 1일 후 부터 선택할 수 있습니다.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else{
                // 각 위젯 정보 가져오기
                str_t_name=contestNameET.text.toString()
                str_t_end_date=yearTextView.text.toString()+"."+monthTextView.text.toString()+"."+dateTextView.text.toString()
                str_t_total_num=personNumET.text.toString()
                var t_total_num=str_t_total_num
                str_t_need_part=needPartET.text.toString()
                str_t_detail=teamIntroET.text.toString()
                str_c_name=contestSpinner.selectedItem.toString()

                // c_num 찾기
                dbManager = DBManager(this, "ContestAppDB", null, 1)
                sqlitedb = dbManager.readableDatabase
                cursor=sqlitedb.rawQuery("SELECT c_num FROM contest WHERE c_name = '"+str_c_name+"';", null)
                if(cursor.moveToNext()){
                    c_num=cursor.getInt(cursor.getColumnIndex("c_num"))
                }
                cursor.close()

                // DB에 정보 넣기
                sqlitedb = dbManager.writableDatabase
                sqlitedb.execSQL("INSERT INTO team (c_num, t_name, t_total_num, t_now_num, t_end_date, t_need_part, t_detail) VALUES ("+c_num+", '"+str_t_name+"', '"+t_total_num+"', "+1+", '"+str_t_end_date+"', '"+str_t_need_part+"', '"+str_t_detail+"')")
                sqlitedb.close()
                dbManager.close()

                this.finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}