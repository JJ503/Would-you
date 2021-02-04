package com.cookandroid.guru2_joinus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class wishListAdapter (val wishlist:ArrayList<Wish>): RecyclerView.Adapter <wishListAdapter.CustomViewHolder>()

{
    // 뷰 연동
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): wishListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_wishlist_item,parent,false)
        return CustomViewHolder(view)
    }

    // 뷰의 데이터 매치 (스크롤 등 때 계속 지원)
    override fun onBindViewHolder(holder: wishListAdapter.CustomViewHolder, position: Int) {
        holder.contestImg.setImageResource(wishlist.get(position).contestImg)
        holder.deadLine.text = wishlist.get(position).deadLine
        holder.contestName.text = wishlist.get(position).contestName
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