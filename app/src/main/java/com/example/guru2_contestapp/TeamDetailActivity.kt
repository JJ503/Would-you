package com.example.guru2_contestapp

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class TeamDetailActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var teamName: TextView
    lateinit var contestName: TextView
    lateinit var section: TextView
    lateinit var endDate: TextView
    lateinit var needPart: TextView
    lateinit var nowNum: TextView
    lateinit var totalNum: TextView
    lateinit var slash: TextView
    lateinit var detail: TextView
    lateinit var applyBtn: Button
    lateinit var commentListArray: ArrayList<CommentListViewItem>
    lateinit var commentItem: CommentListViewItem
    lateinit var commentListView: ListView
    lateinit var commentRegET: EditText
    lateinit var commentRegBtn: Button
    //lateinit var 사진?
    lateinit var cm_name: TextView
    lateinit var cm_date: TextView
    lateinit var cm_detail: TextView

    lateinit var str_teamName: String
    lateinit var str_contestNum: String
    lateinit var str_endDate: String
    lateinit var str_needPart: String
    lateinit var str_nowNum: String
    lateinit var str_totalNum: String
    lateinit var str_detail: String
    lateinit var str_host: String
    lateinit var c_name: String
    lateinit var c_section: String
    lateinit var str_cm_id: String
    lateinit var str_cm_date: String
    lateinit var str_cm_detail: String
    lateinit var str_cm_reg_date: String
    lateinit var str_cm_reg_detail: String
    lateinit var end_date: String
    var state=-1
    var total_num=-1
    var now_num=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

        // 로그인한 계정 아이디
        val sharedPreferences : SharedPreferences = this.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        teamName=findViewById(R.id.Wtdetail_teamNameTextView)
        contestName=findViewById(R.id.Wtdetail_contestNameTextView)
        section=findViewById(R.id.Wtdetail_sectionTextView)
        endDate=findViewById(R.id.Wtdetail_endDateTextView)
        needPart=findViewById(R.id.Wtdetail_needPartTextView)
        nowNum=findViewById(R.id.Wtdetail_nowNumTextView)
        totalNum=findViewById(R.id.Wtdetail_totalNumTextView)
        slash=findViewById(R.id.WslashTextView2)
        detail=findViewById(R.id.Wtdetail_detailTextView)
        applyBtn=findViewById(R.id.Wtd_applyButton)
        commentListView=findViewById(R.id.WcommentListView)
        commentRegBtn=findViewById(R.id.commentRegisterButton)
        commentRegET=findViewById(R.id.commentRegEditText)

        // 이전 페이지에서 넘긴 팀 번호를 intent로 받아 t_num에 저장
        val intent=intent
        val t_num=intent.getIntExtra("intent_t_num", 0)

        // t_num으로 DB에서 팀 정보를 가져와 출력
        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        val cursor2: Cursor
        try {
            if(sqlitedb!=null){
                cursor=sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = '" + t_num + "';", null)
                if(cursor.count!=null){
                    if (cursor.moveToNext()){
                        //var photo
                        str_teamName=cursor.getString(cursor.getColumnIndex("t_name")).toString()
                        str_contestNum=cursor.getInt(cursor.getColumnIndex("c_num")).toString()
                        str_endDate=cursor.getString(cursor.getColumnIndex("t_end_date")).toString()
                        str_needPart=cursor.getString(cursor.getColumnIndex("t_need_part")).toString()
                        str_nowNum=cursor.getInt(cursor.getColumnIndex("t_now_num")).toString()
                        str_totalNum=cursor.getInt(cursor.getColumnIndex("t_total_num")).toString()
                        str_detail=cursor.getString(cursor.getColumnIndex("t_detail")).toString()
                        str_host=cursor.getString(cursor.getColumnIndex("t_host")).toString()

                        //team 테이블이 가진 c_num 값으로 contest 테이블에서 헤당 공모전 정보를 가져옴
                        cursor2=sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = '" + str_contestNum + "';", null)
                        if(cursor2.count!=null){
                            if(cursor2.moveToNext()){
                                c_name=cursor2.getString(cursor2.getColumnIndex("c_name")).toString()
                                c_section=cursor2.getString(cursor2.getColumnIndex("c_section")).toString()
                            }
                            cursor2.close()
                        }
                    }
                }
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }

        teamName.text=str_teamName
        contestName.text=c_name
        section.text=c_section
        endDate.text=str_endDate
        needPart.text=str_needPart
        nowNum.text=str_nowNum
        totalNum.text=str_totalNum
        detail.text=str_detail

        // 남은 인원이 1명 -> 글자색 변경
        val possible_num = str_totalNum.toInt() - str_nowNum.toInt()
        if(possible_num==1){
            nowNum.setTextColor(ContextCompat.getColor(this, R.color.impend))
            slash.setTextColor(ContextCompat.getColor(this, R.color.impend))
            totalNum.setTextColor(ContextCompat.getColor(this, R.color.impend))
        }

        // 현재날짜와 마감일의 날짜 차이 계산
        val today= Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val token=str_endDate.split(".")
        val deadline= Calendar.getInstance().apply {
            set(Calendar.YEAR, token[0].toInt())
            set(Calendar.MONTH, (token[1].toInt()) - 1)
            set(Calendar.DAY_OF_MONTH, token[2].toInt())
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // 마감일이 1일 남음 -> 글자색 변경
        val calcDate=(deadline-today) / (24*60*60*1000)
        if(calcDate.toInt()<=1){
            endDate.setTextColor(ContextCompat.getColor(this, R.color.impend))
        }

        //댓글
        commentListArray= arrayListOf<CommentListViewItem>()
        val cm_contest: View = layoutInflater.inflate(R.layout.comment_list, null, false)
        cm_name= cm_contest.findViewById<TextView>(R.id.WnameTextView)
        cm_date= cm_contest.findViewById<TextView>(R.id.WdateTextView)
        cm_detail= cm_contest.findViewById<TextView>(R.id.WcommentTextView)

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        try {
            if(sqlitedb!=null){
                // comment 테이블에서 t_num으로 해당 팀에 대한 댓글에 대한 정보를 가져와 출력
                cursor=sqlitedb.rawQuery("SELECT * FROM comment WHERE t_num = " + t_num + ";", null)
                str_cm_id=""
                str_cm_date=""
                str_cm_detail=""
                if(cursor.count!=0){
                    while(cursor.moveToNext()){
                        str_cm_id=cursor.getString(cursor.getColumnIndex("m_id")).toString()
                        str_cm_date=cursor.getString(cursor.getColumnIndex("cm_date")).toString()
                        str_cm_detail=cursor.getString(cursor.getColumnIndex("cm_detail")).toString()

                        commentItem= CommentListViewItem("사진", str_cm_id, str_cm_detail, str_cm_date)
                        commentListArray.add(commentItem)
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

        cm_name.text=str_cm_id
        cm_date.text=str_cm_date
        cm_detail.text=str_cm_detail

        // 댓글 작성 버튼 클릭 시, 댓글 내용이 없으면 대화상자로 입력하라는 메시지를 전달
        // 내용이 있는 경우, 입력 내용을 DB에 입력
        commentRegBtn.setOnClickListener {
            if(commentRegET.text.toString()==""){
                val builder= AlertDialog.Builder(this)
                builder.setMessage("댓글 내용을 입력해 주세요.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if(USER_ID.equals(str_host)){ // 댓글 작성자가 팀장이면 아이디 대신 '팀장'
                val currentDateTime= Calendar.getInstance().time
                val dateFormat= SimpleDateFormat("yyyy.MM.dd  HH:mm", Locale.KOREA).format(currentDateTime)
                str_cm_reg_date=dateFormat
                str_cm_reg_detail=commentRegET.text.toString()

                dbManager = DBManager(this, "ContestAppDB", null, 1)
                sqlitedb = dbManager.writableDatabase
                try {
                    if(sqlitedb!=null){
                        sqlitedb.execSQL("INSERT INTO comment (t_num, m_id, cm_date, cm_detail) VALUES(" + t_num + ", '팀장' , '" + str_cm_reg_date + "', '" + str_cm_reg_detail + "')")
                    }
                } catch(e: Exception){
                    Log.e("Error", e.message.toString())
                } finally{
                    sqlitedb.close()
                    dbManager.close()
                }

                // 입력한 댓글이 바로 보일 수 있게 새로고침
                val intent = getIntent()
                finish()
                startActivity(intent)
            }
            else {
                val currentDateTime= Calendar.getInstance().time
                val dateFormat= SimpleDateFormat("yyyy.MM.dd  HH:mm", Locale.KOREA).format(currentDateTime)
                str_cm_reg_date=dateFormat
                str_cm_reg_detail=commentRegET.text.toString()

                dbManager = DBManager(this, "ContestAppDB", null, 1)
                sqlitedb = dbManager.writableDatabase
                try {
                    if(sqlitedb!=null){
                        sqlitedb.execSQL("INSERT INTO comment (t_num, m_id, cm_date, cm_detail) VALUES(" + t_num + ", '" + USER_ID + "', '" + str_cm_reg_date + "', '" + str_cm_reg_detail + "')")

                    }
                } catch(e: Exception){
                    Log.e("Error", e.message.toString())
                } finally{
                    sqlitedb.close()
                    dbManager.close()
                }

                // 입력한 댓글이 바로 보일 수 있게 새로고침
                val intent = getIntent()
                finish()
                startActivity(intent)
            }
        }

        val commentListAdapter=CommentListViewAdapter(this, commentListArray)
        commentListView.adapter=commentListAdapter


        // 댓글 listView 길이에 따라 화면 길이 조정 --> Scroll 안에 ListView가 있는 것이라 이렇게 해주지 않으면 스크롤이 안 됨
        var totalHeight=0

        for(i in 0 until commentListAdapter.getCount()){
            val listItem: View =commentListAdapter.getView(i, null, commentListView)
            listItem.measure(0, 0)
            totalHeight+=listItem.measuredHeight
        }

        val params: ViewGroup.LayoutParams = commentListView.getLayoutParams()
        params.height = totalHeight + (commentListView.getDividerHeight() * (commentListAdapter.getCount() - 1))
        commentListView.setLayoutParams(params)
        commentListView.setAdapter(commentListAdapter)

        // 신청 버튼 클릭 시, 팀 지원 페이지로 공모전 이름과, 팀 이름을 intent로 넘기며 이동함
        applyBtn.setOnClickListener {

            // 지원자가 팀장인 경우 대화상자로 알림
            dbManager = DBManager(this, "ContestAppDB", null, 1)
            sqlitedb = dbManager.readableDatabase
            try {
                if(sqlitedb!=null){
                    cursor=sqlitedb.rawQuery("SELECT state FROM teamManage WHERE t_num = '"+t_num+"' AND m_id = '"+USER_ID+"';", null)
                    if(cursor.count!=0){
                        if(cursor.moveToNext()){
                            state=cursor.getInt(cursor.getColumnIndex("state"))
                        }
                    }

                    cursor=sqlitedb.rawQuery("SELECT t_total_num, t_now_num, t_end_date FROM team WHERE t_num = '"+t_num+"';", null)
                    if(cursor.count!=0){
                        if(cursor.moveToNext()){
                            total_num=cursor.getInt(cursor.getColumnIndex("t_total_num"))
                            now_num=cursor.getInt(cursor.getColumnIndex("t_now_num"))
                            end_date=cursor.getString(cursor.getColumnIndex("t_end_date"))
                        }
                        cursor.close()
                    }
                }
            }catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
                dbManager.close()
            }

            // 남은 인원 계산
            val possible_num=total_num-now_num


            // 마감일과 현재의 날짜차이 계산
            val today= Calendar.getInstance().apply{
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val dateString=end_date
            val token=dateString.split(".")
            val deadline= Calendar.getInstance().apply {
                set(Calendar.YEAR, token[0].toInt())
                set(Calendar.MONTH, (token[1].toInt())-1)
                set(Calendar.DAY_OF_MONTH, token[2].toInt())
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val calcDate=(deadline-today) / (24*60*60*1000)

            val builder= AlertDialog.Builder(this)
            if(state==2){
                builder.setMessage("당신은 이 팀의 팀장입니다.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if(possible_num==0){
                builder.setMessage("인원이 가득 찼습니다.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else if(calcDate<0){
                builder.setMessage("모집일이 지났습니다.")
                //builder.setIcon(R.)
                builder.setPositiveButton("확인", null)
                builder.show()
            } else{
                val intent= Intent(this, ResumeActivity::class.java)
                intent.putExtra("intent_c_name", c_name)
                intent.putExtra("intent_t_name", str_teamName)
                startActivity(intent)
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