package com.example.guru2_contestapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class WishListAdapter (val wishlist:ArrayList<Wish>): RecyclerView.Adapter <WishListAdapter.CustomViewHolder>()

{
    // 뷰 연동
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_wishlist_item,parent,false)
        return CustomViewHolder(view)
    }

    // 뷰의 데이터 매치 (스크롤 등 때 계속 지원)

    override fun onBindViewHolder(holder: WishListAdapter.CustomViewHolder, position: Int) {
        holder.contestImg.setImageResource(wishlist.get(position).contestImg)
        holder.deadLine.text = wishlist.get(position).deadLine
        holder.contestName.text = wishlist.get(position).contestName


        if (wishlist.get(position).deadLine =="모집 종료"){
            holder.deadLine.setTextColor(Color.parseColor("#FF0000"))
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