package com.example.guru2_contestapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.collections.ArrayList

class TeamListViewAdapter (val context: Context, val teamList: ArrayList<TeamListViewItem>): BaseAdapter(){
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
        var possible_num = teamlist.totalNum - teamlist.nowNum
        if(possible_num==1){
            nowNum.setTextColor(ContextCompat.getColor(context,R.color.impend))
            slash.setTextColor(ContextCompat.getColor(context,R.color.impend))
            totalNum.setTextColor(ContextCompat.getColor(context,R.color.impend))
        }

        // 마감일이 1일 남은 경우 -> 글자색 변경
        val today= Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        var dateString=teamlist.endDate
        var token=dateString.split(".")
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
        if(calcDate.toInt()<=1){
            endDate.setTextColor(ContextCompat.getColor(context, R.color.impend))
            magam.setTextColor(ContextCompat.getColor(context, R.color.impend))
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