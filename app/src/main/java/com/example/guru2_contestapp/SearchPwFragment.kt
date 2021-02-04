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

class SearchPwFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_search_id, container, false)

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
                sqlitedb = dbManager.readableDatabase

                if (sqlitedb != null){
                    var cursor : Cursor
                    cursor = sqlitedb.rawQuery("SELECT * FROM member WHERE name = '${input_name}' AND id = '${input_id}';",
                        null)
                    cursor.moveToFirst()

                    if (cursor.getCount() == 1){
                        var USER_EMAIL = cursor.getString(cursor.getColumnIndex("email"))
                        var USER_PW = cursor.getString(cursor.getColumnIndex("pw"))
                        var USER_NAME = cursor.getString(cursor.getColumnIndex("name"))
                        var mailTitle = "[ 앱 이름 ] 비밀번호 확인"
                        var mailBody = USER_NAME + "님의 임시 비밀번호는 " + USER_PW + " 입니다.\n" +
                                "임시 비밀번호로 로그인하시고 비민버호를 변경해주세요."

                        val email = Intent(Intent.ACTION_SEND)
                        email.type = "plain/text"
                        email.putExtra(Intent.EXTRA_USER, "contestApp@gmail.com")
                        email.putExtra(Intent.EXTRA_EMAIL, USER_EMAIL)
                        email.putExtra(Intent.EXTRA_SUBJECT, mailTitle)
                        email.putExtra(Intent.EXTRA_TEXT, mailBody)
                        startActivity(email)

                        builder.setTitle("비밀번호 찾기")
                        builder.setMessage("비밀번호가 문자로 발송 되었습니다.")
                        builder.setPositiveButton("확인", null)
                        builder.show()
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