package com.example.guru2_contestapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class WishListAdapter (val wishlist:ArrayList<WishItem>): RecyclerView.Adapter <WishListAdapter.CustomViewHolder>()

{
    // 뷰 연동
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_wishlist_item,parent,false)
        return CustomViewHolder(view)
    }

    // 뷰의 데이터 매치 (스크롤 등 때 계속 지원)

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: WishListAdapter.CustomViewHolder, position: Int) {
        holder.contestImg.setImageResource(wishlist.get(position).contestImg)
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

        // item(WishItem)클릭시 ContestDetailActivity(공모전 소개)페이지로 넘어간다.
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context,ContestDetailActivity::class.java )
            intent.putExtra("intent_c_num", wishlist.get(position).c_num)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

    }

    //리스트 총 개수
    override fun getItemCount(): Int {
        return wishlist.size
    }

    // 뷰를 잡아줌
    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val contestImg = itemView.findViewById<ImageView>(R.id.contestImg)
        val deadLine = itemView.findViewById<TextView>(R.id.deadLine)
        val contestName = itemView.findViewById<TextView>(R.id.contestName)
    }

}