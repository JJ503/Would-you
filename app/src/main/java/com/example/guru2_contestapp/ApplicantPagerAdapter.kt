package com.example.guru2_contestapp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ApplicantPagerAdapter(val itemList : List<ApplicantPagerItem>) : RecyclerView.Adapter<ApplicantPagerAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val context = parent.context
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.applicant_pager_item, parent, false)
        val viewHolder = CustomViewHolder(view)

        return viewHolder

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.onBind(itemList[position])

        var dbManager: DBManager =  DBManager(holder.itemView.context, "ContestAppDB", null, 1)
        var sqlitedb : SQLiteDatabase = dbManager.writableDatabase

        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
        cursor.moveToFirst()
        var state = cursor.getInt(cursor.getColumnIndex("state"))

        when(state){
            -1 -> {
                holder.announText.text = "거절한 신청자입니다. 복구 하시겠습니까?"
                holder.btnCancel.text = "복구"
                holder.announText.visibility = VISIBLE
                holder.btnCancel.visibility = VISIBLE

                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            1 -> {
                holder.announText.text = "수락한 신청자입니다. 취소 하시겠습니까?"
                holder.btnCancel.text = "취소"
                holder.announText.visibility = VISIBLE
                holder.btnCancel.visibility = VISIBLE

                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            else -> {
                holder.btnAccept.visibility = VISIBLE
                holder.btnRefuse.visibility = VISIBLE
                holder.announText.visibility = GONE
                holder.btnCancel.visibility = GONE
            }
        }

        holder.btnInfo.setOnClickListener {

        }

        holder.btnAccept.setOnClickListener {
            holder.announText.text = "수락한 신청자입니다. 취소 하시겠습니까?"
            holder.btnCancel.text = "취소"
            holder.announText.visibility = VISIBLE
            holder.btnCancel.visibility = VISIBLE

            holder.btnAccept.visibility = GONE
            holder.btnRefuse.visibility = GONE
        }

        holder.btnRefuse.setOnClickListener {
            holder.announText.text = "거절한 신청자입니다. 복구 하시겠습니까?"
            holder.btnCancel.text = "복구"
            holder.announText.visibility = VISIBLE
            holder.btnCancel.visibility = VISIBLE

            holder.btnAccept.visibility = GONE
            holder.btnRefuse.visibility = GONE
        }
    }

    class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvUserName = v.findViewById<TextView>(R.id.JuserName)
        val ageText = v.findViewById<TextView>(R.id.JageText)
        val hopeText = v.findViewById<TextView>(R.id.JhopeText)
        val btnInfo = v.findViewById<Button>(R.id.JbtnInfo)
        val btnAccept = v.findViewById<ImageButton>(R.id.JbtnAccept2)
        val btnRefuse = v.findViewById<ImageButton>(R.id.JbtnRefuse2)
        val announText = v.findViewById<TextView>(R.id.JannounText)
        val btnCancel = v.findViewById<Button>(R.id.JbtnCancel)

        fun onBind(item: ApplicantPagerItem) {
            tvUserName.text = item.m_name
            ageText.text = item.m_age.toString()
            hopeText.text = item.r_hope
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
