package com.example.guru2_contestapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class ProfileListAdapter(val profileItemList: ArrayList<ProfileItem>):RecyclerView.Adapter <ProfileListAdapter.CustomViewHolder>() {

    // 뷰 연동
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_profile_item, parent, false)
        return CustomViewHolder(view)
    }




    // 뷰의 데이터 매치 (스크롤 등때 계속 지원)
    override fun onBindViewHolder(holder: ProfileListAdapter.CustomViewHolder, position: Int) {


        var str_photo =profileItemList.get(position).profile.toString()
        var photo_src=holder.itemView.context.resources.getIdentifier(str_photo,"drawable", "com.example.guru2_contestapp")

        holder.profile.setImageResource(photo_src)


        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("프로필 선택")
            builder.setIcon(R.drawable.logo_2_04)
            builder.setMessage("위 이미지로 선택하시겠습니까?")

            builder.setNeutralButton("다시선택", null)
            builder.setPositiveButton("확인") { dialog, which ->


                // DB 저장
                lateinit var dbManager: DBManager
                lateinit var sqlitedb: SQLiteDatabase


                //현재 로그인 중인 사용자 지정
                var context: Context = holder.itemView?.context
                val sharedPreferences: SharedPreferences =
                    context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
                var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


                dbManager = DBManager(holder.itemView?.context, "ContestAppDB", null, 1)
                sqlitedb = dbManager.readableDatabase
                try {
                    if (sqlitedb != null) {
                        sqlitedb = dbManager.readableDatabase
                        sqlitedb.execSQL(
                            "UPDATE member SET m_profile = '" + str_photo + "' WHERE m_id = '" + USER_ID + "';"
                        )
                        Log.d("----profile---",str_photo)
                        sqlitedb.close()
                    }
                } catch (e: Exception) {
                    Log.e("Error", e.message.toString())
                } finally {
                    sqlitedb.close()
                    dbManager.close()
                }
                Toast.makeText(holder.itemView?.context,"프로필 변경에 성공했습니다.",Toast.LENGTH_SHORT).show()
            }
            builder.show()


        }


    }
    //리스트 총 개수
    override fun getItemCount(): Int {
        return profileItemList.size
    }

    // 뷰를 잡아줌
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<ImageView>(R.id.profile)
    }

}

