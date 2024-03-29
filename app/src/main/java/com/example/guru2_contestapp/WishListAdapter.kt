package com.example.guru2_contestapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class WishListAdapter (val wishlist:ArrayList<WishItem>): RecyclerView.Adapter <WishListAdapter.CustomViewHolder>()

{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_wishlist_item,parent,false)
        return CustomViewHolder(view)
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: WishListAdapter.CustomViewHolder, position: Int) {
        var photo_src = holder.itemView?.context.resources.getIdentifier(wishlist.get(position).contestImg,"drawable", "com.example.guru2_contestapp")
        holder.contestImg.setImageResource(photo_src)
        holder.deadLine.text = wishlist.get(position).deadLine
        holder.contestName.text = wishlist.get(position).contestName


        if (wishlist.get(position).deadLine == "모집 종료"){
            holder.deadLine.setTextColor(Color.parseColor("#FF0000"))
        } else if (wishlist.get(position).deadLine == "팀원 수락"){
            holder.deadLine.setBackgroundColor(Color.parseColor("#17009688"))
        } else if (wishlist.get(position).deadLine == "팀장입니다"){
            holder.deadLine.setBackgroundColor(Color.parseColor("#17009688"))
        } else if (wishlist.get(position).deadLine == "대기 중"){
            holder.deadLine.setBackgroundColor(Color.parseColor("#8DEAEAEA"))
        }

        // item(WishItem)클릭시 contest라면 ContestDetailActivity(공모전 소개)페이지로 넘어간다.
        // team이라면 TeamDetailActivity(팀 소개)페이지로 넘어간다.
        holder.itemView.setOnClickListener {
            if (wishlist.get(position).which == "contest"){
                val intent = Intent(holder.itemView?.context,ContestDetailActivity::class.java )
                intent.putExtra("intent_c_num", wishlist.get(position).c_num)
                ContextCompat.startActivity(holder.itemView.context, intent, null)

            } else if (wishlist.get(position).which == "team"){
                val intent = Intent(holder.itemView?.context,TeamDetailActivity::class.java )
                intent.putExtra("intent_t_num", wishlist.get(position).c_num)
                ContextCompat.startActivity(holder.itemView.context, intent, null)
            } else {
                Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
            }

        }

    }


    override fun getItemCount(): Int {
        return wishlist.size
    }


    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val contestImg = itemView.findViewById<ImageView>(R.id.contestImg)
        val deadLine = itemView.findViewById<TextView>(R.id.deadLine)
        val contestName = itemView.findViewById<TextView>(R.id.contestName)
    }

}