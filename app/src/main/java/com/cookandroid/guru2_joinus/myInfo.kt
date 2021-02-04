package com.cookandroid.guru2_joinus

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ArrayAdapter
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class myInfo : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // 프래그먼트 생성시 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }


    // 프래그먼트 첫 호출시 , 프래그먼트에 UI를 그리기 위해 view 반환
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var preView = inflater.inflate(R.layout.fragment_my_info, container, false)

////////////////////////////////////////////////////////////////
        var name : TextView = preView .findViewById(R.id.name)
        var userId :TextView = preView.findViewById(R.id.userId)
        var birth :TextView = preView.findViewById(R.id.birth)
        var tel : TextView =preView.findViewById(R.id.telEdt)
        var email :TextView =preView.findViewById(R.id.emailEdt)

        var updateBtn : Button = preView.findViewById(R.id.updateBtn)

        lateinit var dbManager : DBManager
        lateinit var sqlitedb : SQLiteDatabase

        var USER_ID :String
        lateinit var str_name :String
        lateinit var str_id :String
        var year: Int =0
        var month: Int =0
        var date: Int =0
        lateinit var str_tel : String
        lateinit var str_email : String

        lateinit var str_job :String
        lateinit var str_univ :String  //이거 null인거 처리
        lateinit var str_area : String
        lateinit var str_interest : String

        //DB 연결
        dbManager = DBManager(getContext(), "ContestAppDB" ,null,1)

        USER_ID="sPPong123"  // 현재 사용자라 가정 (이건 나중에 SESSION 작업 필요)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE id = '" + USER_ID + "';", null)


        if (cursor.moveToNext()) {
            str_name = cursor.getString(cursor.getColumnIndex("name"))
            str_id = cursor.getString(cursor.getColumnIndex("id"))
            year = cursor.getInt(cursor.getColumnIndex("year"))
            month = cursor.getInt(cursor.getColumnIndex("month"))
            date = cursor.getInt(cursor.getColumnIndex("date"))
            str_tel = cursor.getString(cursor.getColumnIndex("tel"))
            str_email = cursor.getString(cursor.getColumnIndex("email"))
            str_job =cursor.getString(cursor.getColumnIndex("job"))
            str_area =cursor.getString(cursor.getColumnIndex("area"))
            str_interest =cursor.getString(cursor.getColumnIndex("interest"))
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        name.text = str_name
        userId.text = str_id
        birth.text = year.toString() +"년 " +month.toString() + "월 " + date.toString() + "일"

        tel.text= str_tel
        email.text= str_email

        /////////////////////////////////



        //스피너 findViewById
        var spinner_job :Spinner = preView.findViewById<Spinner>(R.id.spinner_job) // 스피너 findViewById
        var spinner_area :Spinner = preView.findViewById<Spinner>(R.id.spinner_area)
        var spinner_interest :Spinner = preView.findViewById<Spinner>(R.id.spinner_interests)


        // 스피너에 넣을 데이터 불러오기
        var sData_job = resources.getStringArray(R.array.job_list)  //spinner에 띄울 데이터 리스트
        var sData_area= resources.getStringArray(R.array.local_list)  //spinner에 띄울 데이터 리스트
        var sData_interest = resources.getStringArray(R.array.interests_list)  //spinner에 띄울 데이터 리스트


        // ArrayAdapter 준비
        var adapter_job :ArrayAdapter<Any?> = ArrayAdapter<Any?>(requireContext(),R.layout.spinner_list , sData_job)
        var adapter_area :ArrayAdapter<Any?> = ArrayAdapter<Any?>(requireContext(),R.layout.spinner_list , sData_area)
        var adapter_interest :ArrayAdapter<Any?> = ArrayAdapter<Any?>(requireContext(),R.layout.spinner_list , sData_interest)



        // 스피너 -  ArrayAdapter 연결
        spinner_job.adapter =adapter_job
        spinner_area.adapter =adapter_area
        spinner_interest.adapter =adapter_interest



        // 스티너1 - 초기값 설정
        spinner_job.setSelection(setJobSpiner(str_job))
        spinner_area.setSelection(setAreaSpiner(str_area))
        spinner_interest.setSelection(setInterestSpiner(str_interest))

        //////////////////////////////////
        updateBtn.setOnClickListener {




        }


        // 스피너 - 어댑터 연결 참고 링크 : https://sbe03005dev.tistory.com/entry/Android-Kotlin-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%BD%94%ED%8B%80%EB%A6%B0-Spinner

        return preView


    }



    // 스티너1 - 초기값 설정
    private fun setJobSpiner(str_job:String): Int {
        var job_index : Int =-1
        when(str_job){
            "청소년" -> job_index=0
            "대학생" -> job_index=1
            "일반인" -> job_index=2
            "직장인" -> job_index=3
            "기타" -> job_index=4
            else -> -1
        }
        return job_index
    }

    // 스티너2 - 초기값 설정
    private fun setAreaSpiner(str_area:String): Int {
        var area_index : Int =-1
        when(str_area){
            "서울" -> area_index=0
            "경기" -> area_index=1
            "인천" -> area_index=2
            "부산" -> area_index=3
            "울산" -> area_index=4
            "대구" -> area_index=5
            "대전" -> area_index=6
            "광주" -> area_index=7
            "강원" -> area_index=8
            "경북" -> area_index=9
            "경남" -> area_index=10
            "충북" -> area_index=11
            "충남" -> area_index=12
            "전북" -> area_index=13
            "전남" -> area_index=14
            "제주" -> area_index=15
            else-> -1
        }
        return area_index
    }

    // 스티너3 - 초기값 설정
    private fun setInterestSpiner(str_interest:String): Int {
        var interest_index : Int =-1
        when(str_interest){
            "기획/아이디어" -> interest_index=0
            "광고/마케팅" -> interest_index=1
            "사진/영상/UCC" -> interest_index=2
            "디자인/순수미술/공예" -> interest_index=3
            "네이밍/슬로건" -> interest_index=4
            "캐릭터/만화/게임" -> interest_index=5
            "건축/건설/인테리어" -> interest_index=6
            "과학/공학" -> interest_index=7
            "예체능/패션" -> interest_index=8
            "전시/패스티벌" -> interest_index=9
            "문학/시나리오" -> interest_index=10
            "학술" -> interest_index=11
            "기타" -> interest_index=12
            else-> -1
        }
        return interest_index
    }


    companion object {
       // 프래그먼트 생성가능, 인자로 전달 가능 .
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                myInfo().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}

