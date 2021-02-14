package com.example.guru2_contestapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class PersonalFragment : Fragment() {
    lateinit var tablayout : TabLayout
    lateinit var  viewpager2 : ViewPager2
    lateinit var  settingBtn : ImageButton
    lateinit var profileImage : ImageView
    lateinit var profileBtn : Button


    lateinit var dbManager : DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var user_name: TextView
    lateinit var user_id : TextView
    lateinit var user_job : TextView

    lateinit var str_name :String
    lateinit var str_id :String
    var str_univ :String =""
    var str_job :String =""

    var str_photo :String = "" // 사진 이름
    var profile_src= -1// 사진 경로


    var now_job =""
    var now_profile :String = "" // 확인용(새로고침용) 사진이름

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 옵션 메뉴 사용 true
        setHasOptionsMenu(true)

        var v_personal = inflater.inflate(R.layout.fragment_personal, null)

        profileImage = v_personal.findViewById<ImageView>(R.id.profileImage)
        profileBtn = v_personal.findViewById<Button>(R.id.profileBtn)
        tablayout = v_personal.findViewById<TabLayout>(R.id.tabLayout1)
        viewpager2 = v_personal.findViewById<ViewPager2>(R.id.viewpager)
        viewpager2.adapter = ViewPagerAdapter_Main(requireActivity())  // 뷰페이저 어댑터 지정

        user_name = v_personal.findViewById(R.id.user_name)
        user_id = v_personal.findViewById(R.id.user_id)
        user_job = v_personal.findViewById(R.id.user_job)


        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        //DB연결
        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        try {
            if (sqlitedb != null) {
                var cursor: Cursor
                cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '" + USER_ID + "';", null)

                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        str_name = cursor.getString(cursor.getColumnIndex("m_name")) + " 님"
                        str_id = cursor.getString(cursor.getColumnIndex("m_id"))
                        str_job = cursor.getString(cursor.getColumnIndex("m_job"))

                        str_univ = ""

                        if (cursor.getString(cursor.getColumnIndex("m_univ")) != null) {
                            if (cursor.getString(cursor.getColumnIndex("m_univ")) != "")
                                str_univ = "(" + cursor.getString(cursor.getColumnIndex("m_univ")) + ")"
                        }
                        if (cursor.getString(cursor.getColumnIndex("m_profile")) != null) {
                            if (cursor.getString(cursor.getColumnIndex("m_profile")) != "")
                            str_photo =  cursor.getString(cursor.getColumnIndex("m_profile"))
                                Log.d("dfdfpersonal",str_photo)
                        } else {
                            str_photo =  "profile0"
                        }

                    }
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        } finally {
            sqlitedb.close()
            dbManager.close()
        }

        user_name.text = str_name
        user_id.text = str_id
        user_job.text = str_job + str_univ
        profile_src = this.resources.getIdentifier(str_photo,"drawable", "com.example.guru2_contestapp")
        profileImage.setImageResource(profile_src)

        // 프로필 변경
        profileBtn.setOnClickListener {
            val intent = Intent(activity, SetProfileActivity::class.java)
            startActivity(intent)
        }


            // 설정 화면으로  전환
            settingBtn = v_personal.findViewById(R.id.settingBtn)
            settingBtn.setOnClickListener {
                val intent = Intent(activity, SettingActivity::class.java)
                startActivity(intent)
            }

            // TabLayoutMediator : tablayout과 viewPager 연결
            TabLayoutMediator(tablayout, viewpager2) { tab, position ->
                tab.customView = getTabView(position)
            }.attach()


            return v_personal
        }




        // 커스텀 탭 뷰 inflate
        private fun getTabView(position: Int): View? {
            val inflater =
                getActivity()?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.custom_tab_button, null, false)
            val tab_num = view.findViewById<TextView>(R.id.tab_num)
            val tab_title = view.findViewById<TextView>(R.id.tab_title)

            var BuildTeam_num: Int = 0
            var ApplyTeam_num: Int = 0
            var Carreer_num: Int = 0
            var Wish_num: Int = 0

            // DB에서 정보 불러오기(리사이클러뷰)
            var context: Context = requireContext()
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
            var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

            sqlitedb = dbManager.readableDatabase


            // 각 tab 별로 item의 개수를 DB로 부터 가져옴
            var cursor: Cursor
            cursor = sqlitedb.rawQuery(
                "SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 2;",
                null
            )
            BuildTeam_num = cursor.getCount()

            cursor = sqlitedb.rawQuery(
                "SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state >= -1  AND state < 2;",
                null
            )
            ApplyTeam_num = cursor.getCount()

            cursor = sqlitedb.rawQuery(
                "SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 5;",
                null
            )  //  쿼리1
            Carreer_num = cursor.getCount()

            cursor = sqlitedb.rawQuery(
                "SELECT * FROM wishlist WHERE m_id = '" + USER_ID + "' and state == 1 ;",
                null
            )
            Wish_num = cursor.getCount()

            cursor.close()


            // tab에 숫자랑 텍스트 연결함
            when (position) {
                0 -> {
                    tab_num.text = BuildTeam_num.toString()
                    tab_title.text = "만든 팀"
                }
                1 -> {
                    tab_num.text = ApplyTeam_num.toString()
                    tab_title.text = "신청 목록"
                }
                2 -> {
                    tab_num.text = Carreer_num.toString()
                    tab_title.text = "경력"
                }
                3 -> {
                    tab_num.text = Wish_num.toString()
                    tab_title.text = "관심 목록"
                }
            }
            return view
        }


        // FragmentStateAdapter : Fragment 와 Viewpager연결
        var PAGE_CNT = 4

        private inner class ViewPagerAdapter_Main(fa: FragmentActivity) : FragmentStateAdapter(fa) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> BuildTeamListFragment()
                    1 -> ApplyTeamListFragment()
                    2 -> CareerTeamListFragment()
                    3 -> WishListFragment()
                    else -> ErrorFragment()
                }
            }

            override fun getItemCount(): Int = PAGE_CNT
        }


        // 메뉴 등록
        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.menu_personal, menu)
        }


        // 메뉴 행동 지정
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item?.itemId) {
                //로그아웃
                R.id.action_logout -> {
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("로그아웃")
                    builder.setMessage("지금 로그아웃하시겠어요?")
                    builder.setNeutralButton("취소", null)
                    builder.setPositiveButton(
                        "로그아웃",
                        DialogInterface.OnClickListener { dialog, which ->

                            // 현재 로그인 사용자를 저장해놓은 SharedPreferences 값 clear
                            var context: Context = requireContext()
                            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                                "userid",
                                AppCompatActivity.MODE_PRIVATE
                            )
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString("USER_ID", "")
                            editor.commit()

                            //로그인 페이지로 이동
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                        })

                    builder.show()
                    return true

                }
                // 개발자 문의
                R.id.action_developerSupport -> {
                    val intent = Intent(activity, DeveloperSupportActivity::class.java)
                    startActivity(intent)
                    return true
                }
                //회원 탈퇴
                R.id.action_deleteAccount -> {
                    val intent = Intent(activity, DeleteAccountActivity::class.java)
                    startActivity(intent)
                    return true
                }
            }

            return super.onOptionsItemSelected(item)
        }


    // 직업 정보 혹은 프로필이 변경된 경우 새로고침되도록 한다.
    override fun onResume() {
        super.onResume()

        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        dbManager= DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb=dbManager.readableDatabase
        val cursor1: Cursor
        val cursor2: Cursor



        try {
            if (sqlitedb != null) {

                // 직업 정보 가져오기
                cursor1 = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '" + USER_ID + "';", null)
                cursor1.moveToFirst()
                if(str_job!= null) {
                    now_job = cursor1.getString(cursor1.getColumnIndex("m_job"))
                }
                else{
                    now_job = cursor1.getString(cursor1.getColumnIndex("m_job"))
                    str_job = cursor1.getString(cursor1.getColumnIndex("m_job"))
                }


                // 프로필 정보 가져오기
                cursor2 = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '" + USER_ID + "';", null)
                cursor2.moveToFirst()
                if(str_photo != ""){
                    now_profile = cursor2.getString(cursor2.getColumnIndex("m_profile"))
                }
                else{
                    str_photo = cursor2.getString(cursor2.getColumnIndex("m_profile"))
                    now_profile =  cursor2.getString(cursor2.getColumnIndex("m_profile"))
                }
                cursor1.close()
                cursor2.close()
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }

        if(str_job != now_job || now_profile != str_photo){
            val ft: FragmentTransaction =fragmentManager!!.beginTransaction()
            ft.detach(this)
            ft.attach(this)
            ft.commit()
        }
    }



    }

