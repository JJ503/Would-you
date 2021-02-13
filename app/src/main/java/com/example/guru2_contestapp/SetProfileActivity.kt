package com.example.guru2_contestapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SetProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        supportActionBar?.elevation = 3f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_setProfile)+"</font>")
    


        //현재 로그인 중인 사용자 지정
        var context: Context = this
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")



        // 리사이클러뷰
        var profileItemList: ArrayList<ProfileItem> = ArrayList()


        try {

            for(i in 0..16){
                profileItemList.add(ProfileItem("profile"+i.toString()))
            }

            
        }catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
        }


        // 리사이클러 뷰에 레이아웃 매니저와 어댑터 설정
        var rv_profile: RecyclerView = this.findViewById<RecyclerView>(R.id.rv_profile)
        rv_profile.layoutManager = GridLayoutManager(this, 3)
        rv_profile.setHasFixedSize(true)

        if (intent.hasExtra("from")) {
            if (intent.getStringExtra("from") == "SingUp"){
                rv_profile.adapter = SignUpProfileAdapter(profileItemList)
                Toast.makeText(this, "sign up", Toast.LENGTH_SHORT).show()
            }

        } else {
            rv_profile.adapter = ProfileListAdapter(profileItemList)
        }

    }

    //뒤로가기 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }



}