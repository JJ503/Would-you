package com.example.guru2_contestapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ApplicantListAdapter(val itemList: ArrayList<ApplicantListData>) : RecyclerView.Adapter<ApplicantListAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.applicant_list_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.onBind(itemList.get(position))

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView?.context, ApplicantPagerActivity::class.java)
            intent.putExtra("pos", position)
            intent.putExtra("m_id", itemList.get(position).t_num)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class CustomViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val tvName = itemView.findViewById<TextView>(R.id.JtvName)

        fun onBind(item: ApplicantListData) {
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
}