package com.example.guru2_contestapp

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ContestListViewAdapter(val contestList: ArrayList<ContestListViewItem>): RecyclerView.Adapter <ContestListViewAdapter.CustomViewHolder>() {
    // 뷰 연동
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestListViewAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contest_list, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContestListViewAdapter.CustomViewHolder, position: Int) {
        //나중에 사진 수정
        holder.contestImg.setImageResource(R.drawable.gentlemans_guide)
        holder.contestTitle.text = contestList.get(position).contestName
        holder.hostName.text = contestList.get(position).hostName
        holder.startDay.text = contestList.get(position).startDay
        holder.endDay.text = contestList.get(position).endDay


        // 공모전 목록에서 공모전을 선택하면 해당 공모전의 번호를 intent로 다음 페이지로 보냄
        //         -->  다음 페이지에서 intent 정보로 해당 공모전에 대한 것만 DB에서 가져오도록 함
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ContestDetailActivity::class.java )
            intent.putExtra("intent_c_num", contestList.get(position).num)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    //리스트 총 개수
    override fun getItemCount(): Int {
        return contestList.size
    }

    // 뷰를 잡아줌
    class CustomViewHolder(view : View): RecyclerView.ViewHolder(view){
        val contestImg = view.findViewById<ImageView>(R.id.WcontestImageView)
        val contestTitle = view.findViewById<TextView>(R.id.WcontestNameTextView)
        val hostName =  view.findViewById<TextView>(R.id.WhostNameTextView)
        val startDay = view.findViewById<TextView>(R.id.WstartDateTextView)
        val endDay = view.findViewById<TextView>(R.id.WendDateTextView)
    }

}