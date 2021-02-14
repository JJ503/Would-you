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

        // DB 연결
        var dbManager: DBManager =  DBManager(holder.itemView.context, "ContestAppDB", null, 1)
        var sqlitedb : SQLiteDatabase = dbManager.writableDatabase

        // 팀의 정보 불러오기
        var cursor : Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
        cursor.moveToFirst()
        var state = cursor.getInt(cursor.getColumnIndex("state"))

        // 신청자 상태에 따라 다르게 보임, 상태가 0(신청상태)는 기본 아이템 사용
        when(state){
            -1 -> {   // 거절 : 수락 거절 버튼이 사라지고 정보 보기와 복구 버튼, 안내 문구만 보임
                holder.announText.text = "거절한 신청자입니다. 복구 하시겠습니까?"
                holder.btnCancel.text = "복구"
                holder.announText.visibility = VISIBLE
                holder.btnCancel.visibility = VISIBLE
                holder.btnInfo2.visibility = VISIBLE

                holder.btnInfo.visibility = GONE
                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            1 -> {   // 수락 : 수락 거절 버튼이 사라지고 정보 보기와 취소 버튼, 안내 문구만 보임
                holder.announText.text = "수락한 신청자입니다. 취소 하시겠습니까?"
                holder.btnCancel.text = "수락 취소"
                holder.announText.visibility = VISIBLE
                holder.btnCancel.visibility = VISIBLE
                holder.btnInfo2.visibility = VISIBLE

                holder.btnInfo.visibility = GONE
                holder.btnAccept.visibility = GONE
                holder.btnRefuse.visibility = GONE
            }

            5 -> {   // 완료 : 수락 거절 버튼이 사라지고 정보 보기와 안내 문구만 보임
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

        // 모집이 종료된 경우
        if (itemList.get(position).t_endStatus == 0){
            holder.announText.text = "팀 모집이 종료되었습니다."
            holder.announText.visibility = VISIBLE

            holder.btnCancel.visibility = GONE
            holder.btnAccept.visibility = GONE
            holder.btnRefuse.visibility = GONE
        }

        // 정보보기 버튼을 누르면 커스텀한 대화상자를 통해 신청자의 정보를 볼 수 있다
        holder.btnInfo.setOnClickListener {
            val dialog = ApplicantInfoDialog(holder.itemView.context, itemList.get(position).m_id, itemList.get(position).t_num)
            dialog.infoDlg()
        }

        // 위 버튼과 같은 역할로 수락 거절이 없어지고 버튼이 두 개일 때는 이 버튼이 보인다
        holder.btnInfo2.setOnClickListener {
            val dialog = ApplicantInfoDialog(holder.itemView.context, itemList.get(position).m_id, itemList.get(position).t_num)
            dialog.infoDlg()
        }

        // 수락 버튼을 누른 경우
        // 정상적으로 처리 되면 수락 거절 버튼 없어지고 안내 문구와 취소 버튼이 생긴다
        // 그리고 상태가 0에서 1로 바뀜
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
                    Toast.makeText(holder.itemView.context, "수락 가능 인원이 꽉찼습니다.", Toast.LENGTH_SHORT).show()
                }

            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{

            }
        }

        // 거절 버튼을 누른 경우
        // 정상적으로 처리 되면 수락 거절 버튼 없어지고 안내 문구와 복구 버튼이 생긴다
        // 상태가 0에서 -1로 바뀜
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

        // 수락 취소 or 거절 복구 버튼
        // 수락 취소 : 신청자의 상태를 1에서 0으로 바뀌게 만든다
        // 거절 복구 : 신청자의 상태를 -1에서 0으로 바뀌게 만든다
        // 공통적으로 취소/복구 버튼이 사라지고 다시 수락 거절 버튼이 생긴다
        holder.btnCancel.setOnClickListener{
            try {
                cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
                cursor.moveToFirst()

                if (cursor.getCount() == 1){
                    if (cursor.getInt(cursor.getColumnIndex("state")) == 1){
                        var t_cursor = sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = ${itemList.get(position).t_num};", null)
                        t_cursor.moveToFirst()

                        if (t_cursor.getCount() == 1) {
                            var now_num = t_cursor.getInt(t_cursor.getColumnIndex("t_now_num"))
                            sqlitedb.execSQL("UPDATE team SET t_now_num = ${now_num - 1} WHERE t_num = ${itemList.get(position).t_num};")
                        } else {
                            Toast.makeText(holder.itemView.context, "오류가 발생했습니다. 문의 부탁드립니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    sqlitedb.execSQL("UPDATE teamManage SET state = 0 WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';")
                    holder.btnAccept.visibility = VISIBLE
                    holder.btnRefuse.visibility = VISIBLE
                    holder.btnInfo.visibility = VISIBLE

                    holder.announText.visibility = GONE
                    holder.btnCancel.visibility = GONE
                    holder.btnInfo2.visibility = GONE
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
