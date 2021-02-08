package com.example.guru2_contestapp

import android.app.AlertDialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment


class SearchIdFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_search_id, container, false)

        var dbManager: DBManager = DBManager(activity, "ContestAppDB", null, 1)
        lateinit var sqlitedb : SQLiteDatabase

        var searchnameEditText : EditText = view.findViewById(R.id.JsearchnameEditText)
        var searchtelEditText : EditText = view.findViewById(R.id.JsearchtelEditText)
        var idSearchButton : Button = view.findViewById(R.id.JidSearchButton)

        var builder = AlertDialog.Builder(activity)

        idSearchButton.setOnClickListener {
            var input_name = searchnameEditText.text.toString()
            var input_tel = searchtelEditText.text.toString()

            try{
                sqlitedb = dbManager.readableDatabase

                if (sqlitedb != null){
                    var cursor : Cursor
                    cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_name = '${input_name}' AND m_tel = '${input_tel}';", null)
                    cursor.moveToFirst()

                    if (cursor != null){
                        builder.setTitle("아이디 찾기")
                        builder.setMessage(cursor.getString(cursor.getColumnIndex("m_id")))
                        builder.setPositiveButton("확인", null)
                        builder.show()
                    } else{
                        Toast.makeText(activity, "해당 계정은 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
            }
        }

        return view
    }
}