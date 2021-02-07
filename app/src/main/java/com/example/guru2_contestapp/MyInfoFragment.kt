package com.example.guru2_contestapp

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream


class MyInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v_myInfo = inflater.inflate(R.layout.fragment_my_info, container, false)


        var name : TextView = v_myInfo .findViewById(R.id.name)
        var userId :TextView = v_myInfo.findViewById(R.id.userId)
        var birth :TextView = v_myInfo.findViewById(R.id.birth)
        var tel : TextView =v_myInfo.findViewById(R.id.telEdt)
        var email :TextView =v_myInfo.findViewById(R.id.emailEdt)
        var univerTableRow : TableRow = v_myInfo.findViewById(R.id.univerTableRow)
        var updateBtn : Button = v_myInfo.findViewById(R.id.updateBtn)

        lateinit var dbManager : DBManager
        lateinit var sqlitedb : SQLiteDatabase


        lateinit var str_name :String
        lateinit var str_id :String
        lateinit var year: String
        lateinit var month: String
        lateinit var date: String
        lateinit var str_tel : String
        lateinit var str_email_id : String
        lateinit var str_email_site : String

        lateinit var str_job :String
        var str_univ :String  =""
        lateinit var str_area : String
        lateinit var str_interest : String



        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb =dbManager.readableDatabase
        try {
            if (sqlitedb != null) {

                var cursor: Cursor
                cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE id = '" + USER_ID + "';", null)
                if (cursor.getCount() != 0) {
                    if (cursor.moveToNext()) {
                        str_name = cursor.getString(cursor.getColumnIndex("name"))
                        str_id = cursor.getString(cursor.getColumnIndex("id"))
                        year = cursor.getString(cursor.getColumnIndex("year"))
                        month = cursor.getString(cursor.getColumnIndex("month"))
                        date = cursor.getString(cursor.getColumnIndex("date"))
                        str_tel = cursor.getString(cursor.getColumnIndex("tel"))
                        str_email_id = cursor.getString(cursor.getColumnIndex("email")).split("@")[0]
                        str_email_site = cursor.getString(cursor.getColumnIndex("email")).split("@")[1]
                        str_job = cursor.getString(cursor.getColumnIndex("job"))
                        if (cursor.getString(cursor.getColumnIndex("univ")) != null) {
                            univerTableRow.visibility=View.VISIBLE
                            str_univ = cursor.getString(cursor.getColumnIndex("univ"))
                        }
                        str_area = cursor.getString(cursor.getColumnIndex("area"))
                        str_interest = cursor.getString(cursor.getColumnIndex("interest"))
                    }
                }
                cursor.close()
            }
        }catch(e: Exception){
        Log.e("Error", e.message.toString())
    } finally{
        sqlitedb.close()
        dbManager.close()
    }


        name.text = str_name
        userId.text = str_id
        birth.text = year + "년 " +month+ "월 " + date + "일"

        tel.text= str_tel
        email.text= str_email_id

        /////////////////////////////////


        //스피너 findViewById
        var spinner_job :Spinner = v_myInfo.findViewById<Spinner>(R.id.spinner_job) // 스피너 findViewById
        var spinner_area :Spinner = v_myInfo.findViewById<Spinner>(R.id.spinner_area)
        var spinner_interest :Spinner = v_myInfo.findViewById<Spinner>(R.id.spinner_interests)
        var spinner_email :Spinner = v_myInfo.findViewById<Spinner>(R.id.spinner_email)

        // 스티너 - 초기값 설정
        spinner_email.setSelection(setEmailSpinner(str_email_site))
        spinner_job.setSelection(setJobSpinner(str_job))
        spinner_area.setSelection(setAreaSpinner(str_area))
        spinner_interest.setSelection(setInterestSpinner(str_interest))


        // '직업' 스피너에서 대학생이 아닐때 -> 대학생 tableRow 없애줘야 함


        // '수정' 버튼 눌렀을 때
        updateBtn.setOnClickListener {




        }


        return v_myInfo
    }


    // 스피너1 - 초기값 설정
    private fun setEmailSpinner(str_job:String): Int {
        var job_index : Int =-1
        when(str_job){
            "gmail.com" -> job_index=1
            "naver.com" -> job_index=2
            "daum.net" -> job_index=3
            "기타" -> job_index=4
            else -> -1
        }
        return job_index
    }




    // 스티너2 - 초기값 설정
    private fun setJobSpinner(str_job:String): Int {
        var job_index : Int =-1
        when(str_job){
            "청소년" -> job_index=1
            "대학생" -> job_index=2
            "일반인" -> job_index=3
            "직장인" -> job_index=4
            "기타" -> job_index=5
            else -> -1
        }
        return job_index
    }

    // 스티너3 - 초기값 설정
    private fun setAreaSpinner(str_area:String): Int {
        var area_index : Int =-1
        when(str_area){
            "서울" -> area_index=1
            "경기" -> area_index=2
            "인천" -> area_index=3
            "부산" -> area_index=4
            "울산" -> area_index=5
            "대구" -> area_index=6
            "대전" -> area_index=7
            "광주" -> area_index=8
            "강원" -> area_index=9
            "경북" -> area_index=10
            "경남" -> area_index=11
            "충북" -> area_index=12
            "충남" -> area_index=13
            "전북" -> area_index=14
            "전남" -> area_index=15
            "제주" -> area_index=16
            else-> -1
        }
        return area_index
    }

    // 스티너4 - 초기값 설정
    private fun setInterestSpinner(str_interest:String): Int {
        var interest_index : Int =-1
        when(str_interest){
            "기획/아이디어" -> interest_index=1
            "광고/마케팅" -> interest_index=2
            "사진/영상/UCC" -> interest_index=3
            "디자인/순수미술/공예" -> interest_index=4
            "네이밍/슬로건" -> interest_index=5
            "캐릭터/만화/게임" -> interest_index=6
            "건축/건설/인테리어" -> interest_index=7
            "과학/공학" -> interest_index=8
            "예체능/패션" -> interest_index=9
            "전시/패스티벌" -> interest_index=10
            "문학/시나리오" -> interest_index=11
            "학술" -> interest_index=12
            "기타" -> interest_index=13
            else-> -1
        }
        return interest_index
    }




}

