package com.cookandroid.guru2_joinus

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    lateinit var tablayout :TabLayout
    lateinit var  viewpager2 :ViewPager2
    lateinit var  settingBtn : ImageButton
    lateinit var profileImage : ImageView
    lateinit var profileBtn : Button


    lateinit var dbManager : DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var user_name: TextView
    lateinit var user_id :TextView
    lateinit var user_job : TextView


    lateinit var str_name :String
    lateinit var str_id :String
    lateinit var str_job :String
    lateinit var str_univ :String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileImage =findViewById<CircleImageView>(R.id.profileImage)
        profileBtn =findViewById<Button>(R.id.profileBtn)
        tablayout=findViewById<TabLayout>(R.id.tabLayout1)
        viewpager2=findViewById<ViewPager2>(R.id.viewpager)
        viewpager2.adapter =ViewPagerAdapter_Main(this)  // 어댑터 지정해주자.. 이거 안하면 오류남 ㅋㅋ



        user_name =findViewById(R.id.user_name)
        user_id =findViewById(R.id.user_id)
        user_job =findViewById(R.id.user_job)


        //DB 연결
        dbManager = DBManager(this, "ContestAppDB", null, 1)

        var USER_ID:String="sPPong123"  // 현재 사용자라 가정 (이건 나중에 SESSION 작업 필요)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE id = '" + USER_ID + "';", null)


        if (cursor.moveToNext()) {
            str_name = cursor.getString(cursor.getColumnIndex("name"))
            str_id = cursor.getString(cursor.getColumnIndex("id"))
            str_job = cursor.getString(cursor.getColumnIndex("job"))
            str_univ = cursor.getString(cursor.getColumnIndex("univ")) //이거 NULL 일때는 오류임 (이거 수정 필요)
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        user_name.text = str_name
        user_id.text = str_id
        user_job.text = str_job  +"(" +str_univ +")" //univ NULL 일때는 오류임 (이거 수정 필요)



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
        settingBtn = findViewById(R.id.settingBtn)
        settingBtn.setOnClickListener {
            val intent =  Intent(this, Setting::class.java)
            startActivity(intent)
         }



        // TabLayoutMediator : tablayout과 viewPager 연결
        TabLayoutMediator(tablayout, viewpager2){ tab, position->
            tab.customView=getTabView(position)
        }.attach()


    }


    // 사진 비트맵으로 imageview에 띄움
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //출처 : https://taekwang.tistory.com/2

        var REQUEST_GALLERY_TAKE =2
//        Log.d("아니 이거 왜 안돼","onAcivity")
        when (requestCode){
            2 -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY_TAKE) {
                    //profileImage.setImageURI(data?.data) // handle chosen image
                    var currentImageUrl: Uri? = data?.data

                    try {
                        // 갤러리에서 불러온 image URL를 bitmap으로 바꿈
                        val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
                        //imageview에 적용
                        profileImage.setImageBitmap(bitmap)


                        //DB 연결
                        dbManager = DBManager(this, "ContestAppDB", null, 1)

                        var USER_ID:String="sPPong123"  // 현재 사용자라 가정 (이건 나중에 SESSION 작업 필요)
                        sqlitedb = dbManager.writableDatabase
                        sqlitedb.execSQL("UPDATE member SET profile = '" + bitmap + "' WHERE id = '"
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



/*
    private fun drawableToByteArray(drawable: Drawable?) : ByteArray?{
        val bitmapDrawable = drawable as BitmapDrawable?
        val bitmap = bitmapDrawable?.bitmap
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG,100, stream)
        Byte [] data = stream.toByteArray()
    }
*/
//////////////////////////////




    // 커스텀 탭 뷰 inflate
    private fun getTabView(position: Int): View? {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_tab_button, null, false)
        val tab_num =view.findViewById<TextView>(R.id.tab_num)
        val tab_title=view.findViewById<TextView>(R.id.tab_title)

        var createTeam_num: Int = 0
        var applyTeam_num: Int = 0
        var carreer_num: Int = 0
        var wishlist_num: Int = 0

        // DB에서 정보 불러오기(리사이클러뷰)
        lateinit var dbManager : DBManager
        lateinit var sqlitedb : SQLiteDatabase

        var USER_ID:String="sPPong123"  // 현재 사용자라 가정 (이건 나중에 SESSION 작업 필요)
        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb =dbManager.readableDatabase

        // 각 tab 별로 item의 개수를 DB로 부터 가져옴
        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT COUNT(*) FROM teamManage WHERE id = '" + USER_ID + "' AND state == 2;", null)
        createTeam_num = cursor.getCount()

        cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE id = '" + USER_ID + "' AND state >= -1  AND state < 2;", null)
        applyTeam_num = cursor.getCount()

        cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE id = '" + USER_ID + "' AND state == 5;", null)  //  쿼리1
        carreer_num = cursor.getCount()

        cursor = sqlitedb.rawQuery("SELECT * FROM wishlist WHERE id = '" + USER_ID + "';", null)

        wishlist_num = cursor.getCount()
        cursor.close()


        // tab에 숫자랑 텍스트 연결함
        when(position){
            0 -> {
                tab_num.text = createTeam_num.toString()
                tab_title.text = "만든 팀"
            }
            1 -> {
                tab_num.text = applyTeam_num.toString()
                tab_title.text = "신청 목록"
            }
            2 -> {
                tab_num.text = carreer_num.toString()
                tab_title.text = "경력"
            }
            3 -> {
                tab_num.text = wishlist_num.toString()
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
                //0 -> ItemFragment()
                0 -> createTeamFragment()
                1 -> applyTeamFragment()
                2 -> careerFragment()
                3 -> wishlistFragment()
                else -> ErrorFragment()
            }
        }
        override fun getItemCount():Int = PAGE_CNT
    }

    private fun loadImage(){
        val intent= Intent()
        intent.type="image/*"
        intent.action = Intent.ACTION_GET_CONTENT


    }


}

