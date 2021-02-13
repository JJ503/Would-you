package com.example.guru2_contestapp

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*

class SearchPwFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_search_pw, container, false)

        var dbManager: DBManager = DBManager(activity, "ContestAppDB", null, 1)
        lateinit var sqlitedb : SQLiteDatabase

        var searchnameEditText2 : EditText = view.findViewById(R.id.JsearchnameEditText2)
        var searchidEditText : EditText = view.findViewById(R.id.JsearchidEditText)
        var pwSearchButton : Button = view.findViewById(R.id.JpwSearchButton)

        var builder = AlertDialog.Builder(activity)

        pwSearchButton.setOnClickListener {
            var input_name = searchnameEditText2.text.toString()
            var input_id = searchidEditText.text.toString()

            try{
                sqlitedb = dbManager.writableDatabase

                if (sqlitedb != null){
                    var cursor : Cursor
                    cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE m_name = '${input_name}' AND m_id = '${input_id}';", null)
                    cursor.moveToFirst()

                    if (cursor.getCount() == 1){
                        var password : String = ""
                        var str = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                                        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                                        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
                                        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                                        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                                        'y', 'z', '!', '@', '#', '$', '%', '&', '*', '?')

                        for (i in 1..6){
                            val ranNum = Random().nextInt(70) - 1
                            password = password + str[ranNum]
                        }

                        var USER_NAME = cursor.getString(cursor.getColumnIndex("m_name"))
                        var m_id = cursor.getString(cursor.getColumnIndex("m_id"))
                        sqlitedb.execSQL("UPDATE member SET m_pw = '${password}' WHERE m_id = '${m_id}';")

                        var dialogBody = USER_NAME + "님의 임시 비밀번호는 " + password + " 입니다.\n" +
                                "임시 비밀번호로 로그인하시고 비밀번호를 변경해주세요."

                        builder.setTitle("비밀번호 찾기")
                        builder.setIcon(R.drawable.logo_2_04)
                        builder.setMessage(dialogBody)
                        builder.setPositiveButton("확인", null)
                        builder.show()

                        searchnameEditText2.setText("")
                        searchidEditText.setText("")
                    } else{
                        Toast.makeText(activity, "해당 계정은 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
            }
        }

        return view
    }
}