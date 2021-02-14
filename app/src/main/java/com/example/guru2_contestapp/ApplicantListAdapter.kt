package com.example.guru2_contestapp

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class ApplicantListAdapter(val itemList: ArrayList<ApplicantListItem>) : RecyclerView.Adapter<ApplicantListAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.applicant_list_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.onBind(itemList[position])

        // DB 연결
        var dbManager: DBManager =  DBManager(holder.itemView.context, "ContestAppDB", null, 1)
        var sqlitedb : SQLiteDatabase = dbManager.writableDatabase

        // 공모전이 모집 중이라면 수락 거절 버튼이 보이고
        // 모집이 종료됐다면 수락 거절 버튼이 보이지 않음
        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
        cursor.moveToFirst()
        var state = cursor.getInt(cursor.getColumnIndex("state"))
        if (itemList.get(position).t_endStatus == 0){
            holder.btnAccept.visibility = GONE
            holder.btnRefuse.visibility = GONE
        }

        // 신청자 상태에 따라 다르게 보임, 상태가 0(신청상태)는 기본 아이템 사용
        when(state){
            -1 -> {   // 거절 : 붉은 배경, 수락 거절 버튼 없음
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.refusal))
                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            1 -> {    // 수락 : 푸른 배경, 수락 거절 버튼 없음
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.approval))
                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            5 -> {    // 수락 후 완료(경력) : 푸른 배경, 수락 거절 버튼 없음
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.approval))
                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            else -> {

            }
        }

        // 아이템을 선택한 경우 해당 사람부터 시작할 수 있도록 position을 ApplicantPagerActivity로 넘겨주고 이동
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context, ApplicantPagerActivity::class.java)
            intent.putExtra("pos", position)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

        // 수락 버튼을 누른 경우
        // 정상적으로 처리 되면 푸른 배경, 수락 거절 버튼 없어지고 상태가 0에서 1로 바뀜
        // 만약 이미 인원이 꽉 찬 상태라면 '수락 가능 인원이 꽉찼습니다.' 경고 문구 출력
        holder.btnAccept.setOnClickListener {
            try {
                cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = ${itemList.get(position).t_num};", null)
                cursor.moveToFirst()
                var total_num = cursor.getInt(cursor.getColumnIndex("t_total_num"))
                var now_num = cursor.getInt(cursor.getColumnIndex("t_now_num"))
                var rest_num = total_num - now_num

                if (rest_num > 0){
                    cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
                    cursor.moveToFirst()

                    if (cursor.getCount() == 1){
                        sqlitedb.execSQL("UPDATE teamManage SET state = 1 WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';")
                        sqlitedb.execSQL("UPDATE team SET t_now_num = ${now_num + 1} WHERE t_num = ${itemList.get(position).t_num};")
                        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.approval))
                        holder.btnAccept.visibility = GONE
                        holder.btnRefuse.visibility = GONE
                    } else {
                        Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(holder.itemView.context, "수락 가능 인원이 꽉찼습니다.", Toast.LENGTH_SHORT).show()
                }

            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                val activity : ApplicantListActivity = holder.itemView.context as ApplicantListActivity
                activity.recreate()
            }
        }

        // 거절 버튼을 누른 경우
        // 정상적으로 처리 되면 붉은 배경, 수락 거절 버튼 없어짐
        // 상태가 0에서 -1로 바뀜
        holder.btnRefuse.setOnClickListener {
            try {
                cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
                cursor.moveToFirst()

                if (cursor.getCount() == 1){
                    sqlitedb.execSQL("UPDATE teamManage SET state = -1 WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';")
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.refusal))
                    holder.btnAccept.visibility = GONE
                    holder.btnRefuse.visibility = GONE
                } else {
                    Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                val activity : ApplicantListActivity = holder.itemView.context as ApplicantListActivity
                activity.recreate()
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class CustomViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.JtvName)
        val btnAccept = view.findViewById<ImageButton>(R.id.JbtnAccept)
        val btnRefuse = view.findViewById<ImageButton>(R.id.JbtnRefuse)
        val cardView = view.findViewById<CardView>(R.id.JlistCardView)


        fun onBind(item: ApplicantListItem) {
            tvName.text = item.m_name
        }
    }
}