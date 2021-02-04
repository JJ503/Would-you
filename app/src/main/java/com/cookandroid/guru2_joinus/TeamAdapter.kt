package com.cookandroid.guru2_joinus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeamAdapter(val teamList :ArrayList<Team>):RecyclerView.Adapter <TeamAdapter.CustomViewHolder>()

{

    // 뷰 연동
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamAdapter.CustomViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.fragment_team_item,parent,false)
        return CustomViewHolder(view)
    }

    // 뷰의 데이터 매치 (스크롤 등때 계속 지원)
    override fun onBindViewHolder(holder: TeamAdapter.CustomViewHolder, position: Int) {
        holder.adverImageView.setImageResource(teamList.get(position).c_photo)
        holder.adverTitleTextView.text = teamList.get(position).t_name
        holder.contestNameTextView.text = teamList.get(position).c_name
        holder.nowNumTextView.text = teamList.get(position).t_now_num.toString()
        holder.totalNumTextView.text = teamList.get(position).t_total_num.toString()
        holder.endDateTextView.text = teamList.get(position).t_end_date
        holder.needPartTextivew.text = teamList.get(position).t_need_part


    }

    //리스트 총 개수
    override fun getItemCount(): Int {
        return teamList.size
    }

    // 뷰를 잡아줌
    class CustomViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val adverImageView = itemView.findViewById<ImageView>(R.id.adverImageView)
        val adverTitleTextView = itemView.findViewById<TextView>(R.id.adverTitleTextView)
        val contestNameTextView = itemView.findViewById<TextView>(R.id.contestNameTextView)
        val nowNumTextView = itemView.findViewById<TextView>(R.id.nowNumTextView)
        val totalNumTextView = itemView.findViewById<TextView>(R.id.totalNumTextView)
        val endDateTextView = itemView.findViewById<TextView>(R.id.endDateTextView)
        val needPartTextivew = itemView.findViewById<TextView>(R.id.needPartTextivew)


    }

}