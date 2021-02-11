package com.example.guru2_contestapp


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BuildTeamListAdapter(val teamList :ArrayList<TeamItem>):RecyclerView.Adapter <BuildTeamListAdapter.CustomViewHolder>() {

    // 뷰 연동
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BuildTeamListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_team_item, parent, false)
        return CustomViewHolder(view)
    }

    // 뷰의 데이터 매치 (스크롤 등때 계속 지원)
    override fun onBindViewHolder(holder: BuildTeamListAdapter.CustomViewHolder, position: Int) {
        holder.adverImageView.setImageResource(teamList.get(position).c_photo)
        holder.adverTitleTextView.text = teamList.get(position).t_name
        holder.contestNameTextView.text = teamList.get(position).c_name
        holder.nowNumTextView.text = teamList.get(position).t_now_num.toString()
        holder.totalNumTextView.text = teamList.get(position).t_total_num.toString()
        holder.endDateTextView.text = teamList.get(position).t_end_date
        holder.needPartTextivew.text = teamList.get(position).t_need_part

        // item(teamItem)클릭시 ApplicantListActivitiy(팀 신청자)페이지로 넘어간다.
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context,ApplicantListActivity::class.java )
            intent.putExtra("t_num", teamList.get(position).t_num)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    //리스트 총 개수
    override fun getItemCount(): Int {
        return teamList.size
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


    }

    // 뒤로 가기 설정
    private fun loadImage(){
        val intent= Intent()
        intent.type="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
    }
}

