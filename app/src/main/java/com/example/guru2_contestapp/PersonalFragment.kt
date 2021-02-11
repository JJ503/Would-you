package com.example.guru2_contestapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
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
    lateinit var str_job :String
    var str_univ :String =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 옵션 메뉴 사용 true
        setHasOptionsMenu(true) 

        var v_personal = inflater.inflate(R.layout.fragment_personal, null)

        profileImage =v_personal.findViewById<ImageView>(R.id.profileImage)
        profileBtn =v_personal.findViewById<Button>(R.id.profileBtn)
        tablayout=v_personal.findViewById<TabLayout>(R.id.tabLayout1)
        viewpager2=v_personal.findViewById<ViewPager2>(R.id.viewpager)
        viewpager2.adapter =ViewPagerAdapter_Main(requireActivity())  // 뷰페이저 어댑터 지정


        user_name =v_personal.findViewById(R.id.user_name)
        user_id =v_personal.findViewById(R.id.user_id)
        user_job =v_personal.findViewById(R.id.user_job)




        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        //DB연결
        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb =dbManager.readableDatabase
        try {
            if (sqlitedb != null) {
                var cursor: Cursor
                cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_id = '" + USER_ID + "';", null)

                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        str_name = cursor.getString(cursor.getColumnIndex("m_name")) + " 님"
                        str_id = cursor.getString(cursor.getColumnIndex("m_id"))
                        str_job = cursor.getString(cursor.getColumnIndex("m_job"))
                        if (cursor.getString(cursor.getColumnIndex("m_univ")) != null ) {
                            if (cursor.getString(cursor.getColumnIndex("m_univ")) !="")
                            str_univ = "(" + cursor.getString(cursor.getColumnIndex("m_univ")) + ")"
                        }

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

        user_name.text = str_name
        user_id.text = str_id
        user_job.text = str_job  +str_univ



        // 프로필 변경
        val Gallery = 0
        var REQUEST_GALLERY_TAKE =2
        profileBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(Intent.createChooser(intent,"Load Picture"), REQUEST_GALLERY_TAKE)
        }



        // 설정 화면으로  전환
        settingBtn = v_personal.findViewById(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val intent =  Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }


        // TabLayoutMediator : tablayout과 viewPager 연결
        TabLayoutMediator(tablayout, viewpager2){ tab, position->
            tab.customView=getTabView(position)
        }.attach()


        return v_personal



    }


    // 메뉴 등록
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_personal, menu)
    }

    // 메뉴 행동 지정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            //로그아웃
           R.id.action_logout ->{

               val builder = AlertDialog.Builder(activity)
               builder.setTitle("로그아웃")
               builder.setMessage("지금 로그아웃하시겠어요?")
               builder.setNeutralButton("취소",null)
               builder.setPositiveButton("로그아웃" , DialogInterface.OnClickListener { dialog, which ->

                   // 현재 로그인 사용자를 저장해놓은 SharedPreferences 값 clear
                   var context: Context = requireContext()
                   val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
                   val editor : SharedPreferences.Editor = sharedPreferences.edit()
                   editor.putString("USER_ID", "")
                   editor.commit()

                   //로그인 페이지로 이동
                   val intent = Intent (activity, MainActivity::class.java)
                   startActivity(intent)
               })

               builder.show()
               return true

            }
            // 개발자 문의
            R.id.action_developerSupport ->{
                val intent = Intent (activity, DeveloperSupportActivity::class.java)
                startActivity(intent)
                return true
            }
            //회원 탈퇴
            R.id.action_deleteAccount->{
                val intent = Intent (activity, DeleteAccountActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }






    // 사진 비트맵으로 imageview에 띄움 (수정필요)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //출처 : https://taekwang.tistory.com/2

        var REQUEST_GALLERY_TAKE =2
        when (requestCode){
            2 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_TAKE) {
                    //profileImage.setImageURI(data?.data) // handle chosen image
                    var currentImageUrl: Uri? = data?.data

                    try {
                        // 갤러리에서 불러온 image URL를 bitmap으로 바꿈
                        val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(getActivity()?.contentResolver, currentImageUrl)
                        //imageview에 적용
                        profileImage.setImageBitmap(bitmap)


                        //DB 연결
                        var context: Context = requireContext()
                        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
                        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

                        sqlitedb = dbManager.writableDatabase
                        sqlitedb.execSQL("UPDATE member SET m_profile = '" + bitmap + "' WHERE id = '"
                                + USER_ID+ "';")
                        sqlitedb.close()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }else{
                    Log.d("----이게 되나?------","끄하하하")
                }
            }
        }
    }




    // 커스텀 탭 뷰 inflate
    private fun getTabView(position: Int): View? {
        val inflater = getActivity()?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_tab_button, null, false)
        val tab_num =view.findViewById<TextView>(R.id.tab_num)
        val tab_title=view.findViewById<TextView>(R.id.tab_title)

        var BuildTeam_num: Int = 0
        var ApplyTeam_num: Int = 0
        var Carreer_num: Int = 0
        var Wish_num: Int = 0

        // DB에서 정보 불러오기(리사이클러뷰)

        var context: Context = requireContext()
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        sqlitedb =dbManager.readableDatabase

        // 각 tab 별로 item의 개수를 DB로 부터 가져옴
        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 2;", null)
        BuildTeam_num = cursor.getCount()

        cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state >= -1  AND state < 2;", null)
        ApplyTeam_num = cursor.getCount()

        cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE m_id = '" + USER_ID + "' AND state == 5;", null)  //  쿼리1
        Carreer_num = cursor.getCount()

        cursor = sqlitedb.rawQuery("SELECT * FROM wishlist WHERE m_id = '" + USER_ID + "';", null)
        Wish_num = cursor.getCount()

        cursor.close()


        // tab에 숫자랑 텍스트 연결함
        when(position){
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
    var PAGE_CNT=4
    private inner class ViewPagerAdapter_Main(fa: FragmentActivity): FragmentStateAdapter(fa){
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> BuildTeamListFragment()
                1 -> ApplyTeamListFragment()
                2 -> CareerTeamListFragment()
                3 -> WishListFragment()
                else -> ErrorFragment()
            }
        }
        override fun getItemCount():Int = PAGE_CNT
    }


    // 뒤로 가기 설정
    private fun loadImage(){
        val intent= Intent()
        intent.type="image/*"
        intent.action = Intent.ACTION_GET_CONTENT

    }
}
