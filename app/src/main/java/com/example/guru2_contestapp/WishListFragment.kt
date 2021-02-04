package com.example.guru2_contestapp

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class WishListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var preView = inflater.inflate(R.layout.fragment_wish_list, container, false)


        var t_num: Int = -1
        lateinit var t_name: String
        lateinit var t_end_date: String


        lateinit var wishlist: ArrayList<Wish>
        wishlist = ArrayList()

        // DB에서 정보 불러오기(리사이클러뷰)
        lateinit var dbManager: DBManager
        lateinit var sqlitedb: SQLiteDatabase


        var USER_ID: String = "sPPong123"  // 현재 사용자라 가정 (이건 나중에 SESSION 작업 필요)
        dbManager = DBManager(requireContext(), "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase


        var cursor1: Cursor // 쿼리1
        cursor1 =
            sqlitedb.rawQuery("SELECT * FROM wishlist WHERE id = '" + USER_ID + "';", null)  //
        lateinit var cursor2: Cursor // 쿼리2

        var deadline: Int = -1


        while (cursor1.moveToNext()) {

            t_num = cursor1.getInt(cursor1.getColumnIndex("t_num"))

            cursor2 =
                sqlitedb.rawQuery("SELECT * FROM team WHERE t_num = " + t_num + ";", null) //쿼리2
            if (cursor2.moveToNext()) {
                // team에서 해당 팀 정보 가져오기
                // 공모전 사진도 가져와야됨
                t_name = cursor2.getString(cursor2.getColumnIndex("t_name"))
                t_end_date = cursor2.getString(cursor2.getColumnIndex("t_end_date"))

                deadline = checkDays(t_end_date)


            }

            wishlist.add(
                Wish(
                    "마감 " + deadline.toString() + "일 전",
                    R.drawable.ic_baseline_add_photo_alternate_24,
                    t_name
                )
            )

        }

        cursor1.close()
        cursor2.close()
        sqlitedb.close()
        dbManager.close()


        var rv_wishlist: RecyclerView = preView.findViewById<RecyclerView>(R.id.rv_wishlist)
        rv_wishlist.layoutManager = GridLayoutManager(requireContext(), 3)
        //rv_wishlist.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        rv_wishlist.setHasFixedSize(true)

        rv_wishlist.adapter = WishListAdapter(wishlist)


        return preView
    }

    // 날짜 차이 구하기
    private fun checkDays(t_end_date: String): Int {

        var Eday_arr = t_end_date.split(".")
        val deadline = Calendar.getInstance().apply {
            set(Calendar.YEAR, Eday_arr[0].toInt())
            set(Calendar.MONTH, Eday_arr[1].toInt() - 1)
            set(Calendar.DAY_OF_MONTH, Eday_arr[2].toInt())
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        var today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis


        val calcDate = (deadline - today) / (24 * 60 * 60 * 1000)
        return calcDate.toInt()
    }

}