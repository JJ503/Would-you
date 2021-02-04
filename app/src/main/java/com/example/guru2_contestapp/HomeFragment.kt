package com.example.guru2_contestapp

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeFragment : Fragment() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb : SQLiteDatabase

    lateinit var userName : TextView
    lateinit var name : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_home, container, false)

        var context: Context = requireContext()
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)

        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase

        try{
            var cursor : Cursor
            cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE id = '${USER_ID}';", null)
            cursor.moveToFirst()

            if (cursor.getCount() != 1){
                Toast.makeText(activity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            } else{
                var cursor : Cursor
                cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE id = '${USER_ID}';", null)
                cursor.moveToFirst()

                var USER_NAME = cursor.getString(cursor.getColumnIndex("name"))

                userName = view.findViewById(R.id.userName)
                name = "안녕하세요 " + USER_NAME + " 님"
                userName.setText(name)
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
        }

        return view
    }


}