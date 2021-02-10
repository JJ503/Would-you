package com.example.guru2_contestapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ApplicantPagerAdapter(val itemList : List<ApplicantPagerData>) : RecyclerView.Adapter<ApplicantPagerAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val context = parent.context
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.applicant_pager_item, parent, false)
        val viewHolder = CustomViewHolder(view)

        return viewHolder

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.onBind(itemList[position])

        holder.btnInfo.setOnClickListener {

        }

        holder.btnAccept.setOnClickListener {

        }

        holder.btnRefuse.setOnClickListener {

        }
    }

    class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvUserName = v.findViewById<TextView>(R.id.JuserName)
        val ageText = v.findViewById<TextView>(R.id.JageText)
        val hopeText = v.findViewById<TextView>(R.id.JhopeText)
        val btnInfo = v.findViewById<Button>(R.id.JbtnInfo)
        val btnAccept = v.findViewById<ImageButton>(R.id.JbtnAccept2)
        val btnRefuse = v.findViewById<ImageButton>(R.id.JbtnRefuse2)

        fun onBind(item: ApplicantPagerData) {
            tvUserName.text = item.m_name
            ageText.text = item.m_age.toString()
            hopeText.text = item.r_hope
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
