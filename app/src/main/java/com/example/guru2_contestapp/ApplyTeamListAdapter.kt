package com.example.guru2_contestapp

import android.content.Context
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

class ApplyTeamListAdapter (val applyTeamList :ArrayList<ApplyTeamItem>): RecyclerView.Adapter <ApplyTeamListAdapter.CustomViewHolder>() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ApplyTeamListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_apply_team_item, parent, false)
        return CustomViewHolder(view)
    }


    override fun onBindViewHolder(holder: ApplyTeamListAdapter.CustomViewHolder, position: Int) {

        holder.adverImageView.setImageResource(applyTeamList.get(position).c_photo)
        holder.adverTitleTextView.text = applyTeamList.get(position).t_name
        holder.contestNameTextView.text = applyTeamList.get(position).c_name
        holder.nowNumTextView.text = applyTeamList.get(position).t_now_num.toString()
        holder.totalNumTextView.text = applyTeamList.get(position).t_total_num.toString()
        holder.endDateTextView.text = applyTeamList.get(position).t_end_date
        holder.needPartTextivew.text = applyTeamList.get(position).t_need_part

        // 현재 로그인 중인 사용자 정보 가져오기
        var context: Context = holder.itemView.context
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        // 사용자가 팀원으로 신청한 것의 합격유무
        var apply_state : Int = applyTeamList.get(position).state
        var str_apply_state : String =""


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
        if(calcDate.toInt()<=1) {
            holder.endDateTextView.setTextColor(Color.parseColor("#F15F5F"))
        }

        // 마감일이 지난 경우(모집마감) -> 날짜 TextView 숨김 / 자동으로  팀원 탈락됨
        if(calcDate.toInt()<0){
            holder.endDateTextView.setTextColor(Color.parseColor("#F15F5F"))
            holder.endDateTextView.text="모집 종료"
            str_apply_state = "탈락"
            holder.apply_state.text = str_apply_state
            apply_state = -1
            holder.applyTeamItem_cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.non_click))

            // DB에 탈락 상태 반영
            dbManager = DBManager(holder.itemView.context, "ContestAppDB", null, 1)
            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("UPDATE TeamManage SET state = -1 WHERE m_id = '" + USER_ID + "'  AND state >= 0  AND state < 2;")
            sqlitedb.close()
        }

        // 남은 인원이 1명 -> 글자색 변경
        val possible_num = applyTeamList.get(position).t_total_num - applyTeamList.get(position).t_now_num
        if(possible_num==1){
            holder.nowNumTextView.setTextColor(Color.parseColor("#F15F5F"))
            holder.slash.setTextColor(Color.parseColor("#F15F5F"))
            holder.totalNumTextView.setTextColor(Color.parseColor("#F15F5F"))
        }


        // 신청 상태를 글자로 바꿔 레이아웃에 반영
        when(apply_state){
            -1 -> {
                str_apply_state = "탈락"
                holder.apply_state.setBackgroundColor(Color.parseColor("#34FF5151"))
            }
            0 -> {
                str_apply_state = "대기 중"
                holder.apply_state.setBackgroundColor(Color.parseColor("#8DEAEAEA"))
            }
            1 -> {
                str_apply_state = "확정"
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


    override fun getItemCount(): Int {
        return applyTeamList.size
    }


    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adverImageView = itemView.findViewById<ImageView>(R.id.adverImageView)
        val adverTitleTextView = itemView.findViewById<TextView>(R.id.adverTitleTextView)
        val contestNameTextView = itemView.findViewById<TextView>(R.id.contestNameTextView)
        val nowNumTextView = itemView.findViewById<TextView>(R.id.nowNumTextView)
        val totalNumTextView = itemView.findViewById<TextView>(R.id.totalNumTextView)
        val endDateTextView = itemView.findViewById<TextView>(R.id.endDateTextView)
        val needPartTextivew = itemView.findViewById<TextView>(R.id.needPartTextivew)
        val apply_state = itemView.findViewById<TextView>(R.id.apply_state)
        val slash = itemView.findViewById<TextView>(R.id.slash)
        val applyTeamItem_cardView = itemView.findViewById<CardView>(R.id.applyTeamItem_cardView)
    }
}

