package com.example.guru2_contestapp

import android.app.Activity
import android.content.Intent
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

        var dbManager: DBManager =  DBManager(holder.itemView.context, "ContestAppDB", null, 1)
        var sqlitedb : SQLiteDatabase = dbManager.writableDatabase

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context, ApplicantPagerActivity::class.java)
            intent.putExtra("pos", position)
            intent.putExtra("t_num", itemList.get(position).t_num)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

        holder.btnAccept.setOnClickListener {
            try {
                var cursor : Cursor
                cursor = sqlitedb.rawQuery("SELECT * FROM teamManage WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';", null)
                cursor.moveToFirst()

                if (cursor.getCount() == 1){
                    sqlitedb.execSQL("UPDATE teamManage SET state = 1 WHERE t_num = ${itemList.get(position).t_num} AND m_id = '${itemList.get(position).m_id}';")
                    holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.approval))
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

        holder.btnRefuse.setOnClickListener {
            try {
                var cursor : Cursor
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

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    // 뒤로 가기 설정
    private fun loadImage(){
        val intent= Intent()
        intent.type="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
    }
}