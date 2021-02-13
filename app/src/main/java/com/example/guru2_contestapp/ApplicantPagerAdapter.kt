package com.example.guru2_contestapp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
                holder.btnInfo2.visibility = VISIBLE

                holder.btnInfo.visibility = GONE
                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            1 -> {
                holder.announText.text = "수락한 신청자입니다. 취소 하시겠습니까?"
                holder.btnCancel.text = "수락 취소"
                holder.announText.visibility = VISIBLE
                holder.btnCancel.visibility = VISIBLE
                holder.btnInfo2.visibility = VISIBLE

                holder.btnInfo.visibility = GONE
                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            5 -> {
                holder.announText.text = "팀 모집이 완료되었습니다."
                holder.announText.visibility = VISIBLE
                holder.btnInfo.visibility = VISIBLE

                holder.btnCancel.visibility = GONE
                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
                holder.btnInfo2.visibility = GONE
            }

            else -> {
                holder.btnAccept.visibility = VISIBLE
                holder.btnRefuse.visibility = VISIBLE
                holder.btnInfo.visibility = VISIBLE

                holder.announText.visibility = GONE
                holder.btnCancel.visibility = GONE
                holder.btnInfo2.visibility = GONE
            }
        }

        if (itemList.get(position).t_endStatus == 0){
            holder.announText.text = "팀 모집이 종료되었습니다."
            holder.announText.visibility = VISIBLE

            holder.btnCancel.visibility = GONE
            holder.btnAccept.visibility = GONE
            holder.btnRefuse.visibility = GONE
        }

        holder.btnInfo.setOnClickListener {
            val dialog = ApplicantInfoDialog(holder.itemView.context, itemList.get(position).m_id, itemList.get(position).t_num)
            dialog.infoDlg()
        }

        holder.btnInfo2.setOnClickListener {
            val dialog = ApplicantInfoDialog(holder.itemView.context, itemList.get(position).m_id, itemList.get(position).t_num)
            dialog.infoDlg()
        }

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
                        holder.announText.text = "수락한 신청자입니다. 취소 하시겠습니까?"
                        holder.btnCancel.text = "수락 취소"
                        holder.announText.visibility = VISIBLE
                        holder.btnCancel.visibility = VISIBLE
                        holder.btnInfo2.visibility = VISIBLE

                        holder.btnInfo.visibility = GONE
                        holder.btnAccept.visibility = GONE
                        holder.btnRefuse.visibility = GONE
                    } else {
                        Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }

            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{

            }
        }

        holder.btnRefuse.setOnClickListener {
            try {
                cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
                cursor.moveToFirst()

                if (cursor.getCount() == 1){
                    sqlitedb.execSQL("UPDATE teamManage SET state = -1 WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';")
                    holder.announText.text = "거절한 신청자입니다. 복구 하시겠습니까?"
                    holder.btnCancel.text = "복구"
                    holder.announText.visibility = VISIBLE
                    holder.btnCancel.visibility = VISIBLE
                    holder.btnInfo2.visibility = VISIBLE

                    holder.btnInfo.visibility = GONE
                    holder.btnAccept.visibility = GONE
                    holder.btnRefuse.visibility = GONE
                } else {
                    Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{

            }
        }

        holder.btnCancel.setOnClickListener{
            try {
                cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
                cursor.moveToFirst()
                Log.d("=== cursor ===", itemList.get(position).t_num.toString())

                if (cursor.getCount() == 1){
                    if (cursor.getInt(cursor.getColumnIndex("state")) == 1){
                        var t_cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = ${itemList.get(position).t_num};", null)
                        t_cursor.moveToFirst()
                        Log.d("=== tm_cursor ===", itemList.get(position).t_num.toString())
                        if (t_cursor.getCount() == 1) {
                            var now_num = t_cursor.getInt(t_cursor.getColumnIndex("t_now_num"))
                            sqlitedb.execSQL("UPDATE team SET t_now_num = ${now_num - 1} WHERE t_num = ${itemList.get(position).t_num};")
                            Log.d("=== cursor ===", "success")
                        } else {
                            Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Log.d("=== cursor ===", "여기 왔니")
                    sqlitedb.execSQL("UPDATE teamManage SET state = 0 WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';")
                    holder.btnAccept.visibility = VISIBLE
                    holder.btnRefuse.visibility = VISIBLE
                    holder.btnInfo.visibility = VISIBLE

                    holder.announText.visibility = GONE
                    holder.btnCancel.visibility = GONE
                    holder.btnInfo2.visibility = GONE
                    Log.d("=== cursor ===", "진짜 성공")
                } else {
                    Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{

            }
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
        val btnInfo2 = v.findViewById<Button>(R.id.JbtnInfo2)
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
