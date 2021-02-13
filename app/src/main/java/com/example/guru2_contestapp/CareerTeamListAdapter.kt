package com.example.guru2_contestapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CareerTeamListAdapter (val careerTeamList :ArrayList<TeamItem>): RecyclerView.Adapter <CareerTeamListAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): CareerTeamListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_team_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CareerTeamListAdapter.CustomViewHolder, position: Int) {
        holder.adverImageView.setImageResource(careerTeamList.get(position).c_photo)
        holder.adverTitleTextView.text = careerTeamList.get(position).t_name
        holder.contestNameTextView.text = careerTeamList.get(position).c_name
        holder.nowNumTextView.text = careerTeamList.get(position).t_now_num.toString()
        holder.totalNumTextView.text = careerTeamList.get(position).t_total_num.toString()
        holder.endDateTextView.text = careerTeamList.get(position).t_end_date
        holder.needPartTextivew.text = careerTeamList.get(position).t_need_part


        // item(TeamItem)클릭시 TeamDetailActivity(팀 소개)페이지로 넘어간다.
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context,TeamDetailActivity::class.java )
            intent.putExtra("intent_t_num", careerTeamList.get(position).t_num)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

    }


    override fun getItemCount(): Int {
        return careerTeamList.size
    }


    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adverImageView = itemView.findViewById<ImageView>(R.id.adverImageView)
        val adverTitleTextView = itemView.findViewById<TextView>(R.id.adverTitleTextView)
        val contestNameTextView = itemView.findViewById<TextView>(R.id.contestNameTextView)
        val nowNumTextView = itemView.findViewById<TextView>(R.id.nowNumTextView)
        val totalNumTextView = itemView.findViewById<TextView>(R.id.totalNumTextView)
        val endDateTextView = itemView.findViewById<TextView>(R.id.endDateTextView)
        val needPartTextivew = itemView.findViewById<TextView>(R.id.needPartTextivew)
    }
}

