package com.example.a0202

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ContestListViewAdapter(val context: Context, val contestList: ArrayList<ContestListViewItem>): BaseAdapter() {
    override fun getCount(): Int {
        return contestList.size
    }

    override fun getItem(position: Int): Any {
        return contestList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.contest_list, null)

        val contestImg = view.findViewById<ImageView>(R.id.WcontestImageView)
        val contestTitle = view.findViewById<TextView>(R.id.WcontestNameTextView)
        val hostName =  view.findViewById<TextView>(R.id.WhostNameTextView)
        val startDay = view.findViewById<TextView>(R.id.WstartDateTextView)
        val endDay = view.findViewById<TextView>(R.id.WendDateTextView)

        val contestlist = contestList[position]

        contestImg.setImageResource(R.drawable.gentlemans_guide)
        contestTitle.text = contestlist.contestName
        hostName.text = contestlist.hostName
        startDay.text = contestlist.startDay
        endDay.text = contestlist.endDay

        return view
    }

}