package com.example.guru2_contestapp

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ApplyTeamListAdapter (val applyTeamList :ArrayList<ApplyTeamItem>): RecyclerView.Adapter <ApplyTeamListAdapter.CustomViewHolder>() {

    // 뷰 연동
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ApplyTeamListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_apply_team_item, parent, false)
        return CustomViewHolder(view)
    }

    // 뷰의 데이터 매치 (스크롤 등때 계속 지원)
    override fun onBindViewHolder(holder: ApplyTeamListAdapter.CustomViewHolder, position: Int) {
        holder.adverImageView.setImageResource(applyTeamList.get(position).c_photo)
        holder.adverTitleTextView.text = applyTeamList.get(position).t_name
        holder.contestNameTextView.text = applyTeamList.get(position).c_name
        holder.nowNumTextView.text = applyTeamList.get(position).t_now_num.toString()
        holder.totalNumTextView.text = applyTeamList.get(position).t_total_num.toString()
        holder.endDateTextView.text = applyTeamList.get(position).t_end_date
        holder.needPartTextivew.text = applyTeamList.get(position).t_need_part

        // 팀 상태에 따라 조정 필요
        val apply_state : Int = applyTeamList.get(position).state
        var str_apply_state : String =""

        when(apply_state){
            -1 -> {
                str_apply_state = "팀원 탈락"
                holder.apply_state.setBackgroundColor(Color.parseColor("#34FF5151"))
            }
            0 -> {
                str_apply_state = "대기 중"
                holder.apply_state.setBackgroundColor(Color.parseColor("#8DEAEAEA"))
            }
            1 -> {
                str_apply_state = "팀원 확정"
                holder.apply_state.setBackgroundColor(Color.parseColor("#17009688"))
            }
            else -> str_apply_state ="오류 발생"
        }

       holder.apply_state.text = str_apply_state




        // item(applyTeam)클릭시 TeamDetailActivity(팀 소개)페이지로 넘어간다.
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context,TeamDetailActivity::class.java )
            intent.putExtra("intent_t_num", applyTeamList.get(position).t_num)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }


    }

    //리스트 총 개수
    override fun getItemCount(): Int {
        return applyTeamList.size
    }

    // 뷰를 잡아줌
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adverImageView = itemView.findViewById<ImageView>(R.id.adverImageView)
        val adverTitleTextView = itemView.findViewById<TextView>(R.id.adverTitleTextView)
        val contestNameTextView = itemView.findViewById<TextView>(R.id.contestNameTextView)
        val nowNumTextView = itemView.findViewById<TextView>(R.id.nowNumTextView)
        val totalNumTextView = itemView.findViewById<TextView>(R.id.totalNumTextView)
        val endDateTextView = itemView.findViewById<TextView>(R.id.endDateTextView)
        val needPartTextivew = itemView.findViewById<TextView>(R.id.needPartTextivew)
        val apply_state = itemView.findViewById<TextView>(R.id.apply_state)

    }

    // 뒤로 가기 설정
    private fun loadImage(){
        val intent= Intent()
        intent.type="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
    }
}

