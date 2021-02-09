package com.example.guru2_contestapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ApplicantListAdapter (val list: ArrayList<ApplicantListData>, val itemClick : (ApplicantListData) -> Unit) : RecyclerView.Adapter<ApplicantListAdapter.CustomViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantListAdapter.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.applicant_list_item, parent, false)
        return CustomViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ApplicantListAdapter.CustomViewHolder, position: Int) {
        //holder.tvName.text = list.get(position).name
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CustomViewHolder(v: View, private val itemClick: (ApplicantListData) -> Unit) : RecyclerView.ViewHolder(v){
        val tvName = itemView.findViewById<TextView>(R.id.JtvName)
        var view : View = v

        fun bind(item: ApplicantListData){
            //tvName.text = item.name

            view.setOnClickListener{ itemClick(item) }
        }
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}