package com.example.guru2_contestapp


import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class BuildTeamListAdapter(val buildTeamList: ArrayList<TeamItem>):RecyclerView.Adapter <BuildTeamListAdapter.CustomViewHolder>() {

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

        //현재 로그인 중인 사용자 지정
        var context: Context = holder.itemView.context
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        holder.adverImageView.setImageResource(buildTeamList.get(position).c_photo)
        holder.adverTitleTextView.text = buildTeamList.get(position).t_name
        holder.contestNameTextView.text = buildTeamList.get(position).c_name
        holder.nowNumTextView.text = buildTeamList.get(position).t_now_num.toString()
        holder.totalNumTextView.text = buildTeamList.get(position).t_total_num.toString()
        holder.endDateTextView.text = buildTeamList.get(position).t_end_date
        holder.needPartTextivew.text = buildTeamList.get(position).t_need_part

        //모집 종료면 0, 모집 중이면 1
        var t_endStatus = 1


        // 마감일과 현재의 날짜차이 계산
        val today= Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val dateString= holder.endDateTextView.text.toString()
        val token=dateString.split(".")

        val deadline= Calendar.getInstance().apply {
            set(Calendar.YEAR, token[0].toInt())
            set(Calendar.MONTH, (token[1].toInt())-1)
            set(Calendar.DAY_OF_MONTH, token[2].toInt())
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val calcDate=(deadline-today) / (24*60*60*1000)


        // 마감일이 1일 남은 경우 -> 글자색 변경
        if(calcDate.toInt()<=1){
            holder.endDateTextView.setTextColor(Color.parseColor("#F15F5F"))
        }

        // 마감일이 지난 경우 -> 날짜 TextView 숨김
        if(calcDate.toInt()<0){
            holder.endDateTextView.setTextColor(Color.parseColor("#F15F5F"))
            holder.endDateTextView.text="모집 종료"
            holder.teamItem_cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.non_click))

            t_endStatus = 0
        }

        // 남은 인원이 1명 -> 글자색 변경
        val possible_num = buildTeamList.get(position).t_total_num - buildTeamList.get(position).t_now_num
        if(possible_num==1){
            holder.nowNumTextView.setTextColor(Color.parseColor("#F15F5F"))
            holder.slash.setTextColor(Color.parseColor("#F15F5F"))
            holder.totalNumTextView.setTextColor(Color.parseColor("#F15F5F"))
        }


        // item(teamItem)클릭시 ApplicantListActivitiy(팀 신청자)페이지로 넘어간다.
        holder.itemView.setOnClickListener {
            //t_num, t_endStatus 넘겨주기
            val sharedPreferences : SharedPreferences = holder.itemView?.context.getSharedPreferences("t_num", MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.putInt("t_num", buildTeamList.get(position).t_num)
            editor.putInt("t_endStatus", t_endStatus)
            editor.commit()


            //페이지 이동
            val intent = Intent(holder.itemView?.context,ApplicantListActivity::class.java )
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

    }

    //리스트 총 개수
    override fun getItemCount(): Int {
        return buildTeamList.size
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
        val slash = itemView.findViewById<TextView>(R.id.slash)
        val teamItem_cardView = itemView.findViewById<CardView>(R.id.teamItem_cardView)
    }

}

