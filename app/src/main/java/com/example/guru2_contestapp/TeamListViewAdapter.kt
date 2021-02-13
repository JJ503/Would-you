package com.example.guru2_contestapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class TeamListViewAdapter (val teamList: ArrayList<TeamListViewItem>): RecyclerView.Adapter <TeamListViewAdapter.CustomViewHolder>() {
    // 뷰 연동
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamListViewAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.team_list, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamListViewAdapter.CustomViewHolder, position: Int) {
        var photo_src=teamList.get(position).img
        holder.contestImg.setImageResource(photo_src)
        holder.teamName.text = teamList.get(position).teamName
        holder.contestName.text = teamList.get(position).contestNum
        holder.endDate.text = teamList.get(position).endDate
        holder.needPart.text = teamList.get(position).needPart
        holder.nowNum.text = teamList.get(position).nowNum.toString()
        holder.totalNum.text = teamList.get(position).totalNum.toString()

        // 남은 인원이 1명 -> 글자색 변경
        // 남은 인원이 0명 -> 모집 종료, 글자색 변경
        val possible_num = teamList.get(position).totalNum - teamList.get(position).nowNum
        if(possible_num==1){
            holder.nowNum.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.impend))
            holder.slash.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.impend))
            holder.totalNum.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.impend))
        }else if (possible_num==0){
            holder.endDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.impend))
            holder.endDate.text="모집 종료"
            holder.magam.visibility=View.GONE
        }

        // 마감일과 현재의 날짜차이 계산
        val today= Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val dateString=teamList.get(position).endDate
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
            holder.endDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.impend))
            holder.magam.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.impend))
        }

        // 마감일이 지난 경우 -> 날짜 TextView 숨김
        if(calcDate.toInt()<0){
            holder.endDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.impend))
            holder.endDate.text="모집 종료"
            holder.magam.visibility=View.GONE
            holder.cardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.non_click))
        }


        // 팀 목록에서 팀을 선택하면 intent로 팀 번호를 팀 상세 페이지로 넘긴다.
        holder.itemView.setOnClickListener {
            val intent= Intent(holder.itemView.context, TeamDetailActivity::class.java)
            intent.putExtra("intent_t_num", teamList.get(position).teamNum)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    //리스트 총 개수
    override fun getItemCount(): Int {
        return teamList.size
    }

    // 뷰를 잡아줌
    class CustomViewHolder(view : View): RecyclerView.ViewHolder(view){
        val contestImg = view.findViewById<ImageView>(R.id.WadverImageView)
        val teamName = view.findViewById<TextView>(R.id.WadverTitleTextView)
        val contestName = view.findViewById<TextView>(R.id.WcontestNameTextView)
        val endDate = view.findViewById<TextView>(R.id.WendDateTextView2)
        val needPart =  view.findViewById<TextView>(R.id.WneedPartTextivew)
        val nowNum =  view.findViewById<TextView>(R.id.WnowNumTextView)
        val totalNum =  view.findViewById<TextView>(R.id.WtotalNumTextView)
        val slash = view.findViewById<TextView>(R.id.WslashTextView)
        val magam = view.findViewById<TextView>(R.id.WtextView16)
        val cardview = view.findViewById<CardView>(R.id.Wcardview)
    }

}
/*
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.team_list, null)

        val contestImg = view.findViewById<ImageView>(R.id.WadverImageView)
        val teamName = view.findViewById<TextView>(R.id.WadverTitleTextView)
        val contestName = view.findViewById<TextView>(R.id.WcontestNameTextView)
        val endDate = view.findViewById<TextView>(R.id.WendDateTextView2)
        val needPart =  view.findViewById<TextView>(R.id.WneedPartTextivew)
        val nowNum =  view.findViewById<TextView>(R.id.WnowNumTextView)
        val totalNum =  view.findViewById<TextView>(R.id.WtotalNumTextView)
        val slash = view.findViewById<TextView>(R.id.WslashTextView)
        val magam = view.findViewById<TextView>(R.id.WtextView16)

        val teamlist = teamList[position]

        contestImg.setImageResource(R.drawable.airport_baby)
        teamName.text = teamlist.teamName
        contestName.text = teamlist.contestNum
        endDate.text = teamlist.endDate
        needPart.text = teamlist.needPart
        nowNum.text = teamlist.nowNum.toString()
        totalNum.text = teamlist.totalNum.toString()

        // 남은 인원이 1명 -> 글자색 변경
        // 남은 인원이 0명 -> 모집 종료, 글자색 변경
        val possible_num = teamlist.totalNum - teamlist.nowNum
        if(possible_num==1){
            nowNum.setTextColor(ContextCompat.getColor(context,R.color.impend))
            slash.setTextColor(ContextCompat.getColor(context,R.color.impend))
            totalNum.setTextColor(ContextCompat.getColor(context,R.color.impend))
        }else if (possible_num==0){
            endDate.setTextColor(ContextCompat.getColor(context,R.color.impend))
            endDate.text="모집 종료"
            magam.visibility=View.GONE
        }

        // 마감일과 현재의 날짜차이 계산
        val today= Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val dateString=teamlist.endDate
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
            endDate.setTextColor(ContextCompat.getColor(context, R.color.impend))
            magam.setTextColor(ContextCompat.getColor(context, R.color.impend))
        }

        // 마감일이 지난 경우 -> 날짜 TextView 숨김
        if(calcDate.toInt()<0){
            endDate.setTextColor(ContextCompat.getColor(context, R.color.impend))
            endDate.text="모집 종료"
            magam.visibility=View.GONE
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.non_click))
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return teamList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return teamList.size
    }

}
*/