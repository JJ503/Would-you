package com.example.guru2_contestapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class SignUpProfileAdapter (val profileItemList: ArrayList<ProfileItem>): RecyclerView.Adapter <SignUpProfileAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignUpProfileAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_profile_item, parent, false)
        return SignUpProfileAdapter.CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: SignUpProfileAdapter.CustomViewHolder, position: Int) {
        holder.profile.setImageResource(profileItemList.get(position).profile)

        // item(teamItem)클릭시 배경색을 회색으로 한다.
        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("프로필 선택")
            builder.setIcon(R.drawable.logo_2_04)
            builder.setMessage("위 이미지로 선택하시겠습니까?")

            builder.setNeutralButton("다시선택", null)
            builder.setPositiveButton("확인") { dialog, which ->

                var profile = profileItemList.get(position).profile

                val intent = Intent(holder.itemView?.context, SignUpActivity::class.java)
                intent.putExtra("profile", profile)

                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }

            builder.show()
        }
    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<ImageView>(R.id.profile)
    }

    override fun getItemCount(): Int {
        return profileItemList.size
    }
}