package com.example.guru2_contestapp

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService


class ChangePwFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var preView = inflater.inflate(R.layout.fragment_change_pw, container, false)


        var currentPw: EditText = preView.findViewById(R.id.currentPw)
        var newPw1: EditText = preView.findViewById(R.id.newPw1)
        var newPw2: EditText = preView.findViewById(R.id.newPw2)
        var changePwBtn: Button = preView.findViewById(R.id.changePwBtn)
        lateinit var userPw: String

        lateinit var dbManager: DBManager
        lateinit var sqlitedb: SQLiteDatabase

        //DB 연결
        dbManager = DBManager(getContext(), "ContestAppDB", null, 1)

        var USER_ID: String = "sPPong123"  // 현재 사용자라 가정 (이건 나중에 SESSION 작업 필요)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT pw FROM member WHERE id = '" + USER_ID + "';", null)


        if (cursor.moveToNext()) {
            userPw = cursor.getString(cursor.getColumnIndex("pw"))
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 버튼 클릭시 팝업창 열기
        changePwBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("비밀번호 변경")

            // '현재 비밀번호' 입력 안 했을 때
            if (currentPw.getText().toString().equals("") || currentPw.getText()
                    .toString() == null
            ) {
                builder.setMessage("현재 비밀번호를 입력하세요.")
            } else {
                // '현재 비밀번호'가 아닐 때
                if (userPw != currentPw.getText().toString()) {
                    builder.setMessage("현재 비밀번호가 아닙니다.")
                } else {
                    if (newPw1.getText().toString().equals("") || newPw1.getText()
                            .toString() == null || newPw2.getText().toString()
                            .equals("") || newPw2.getText().toString() == null
                    ) {
                        builder.setMessage("변경할 비밀번호를 입력하세요. (확인란도 작성)")
                        //Toast.makeText(getContext(), "'" + newPw1.getText().toString() + "'", Toast.LENGTH_LONG).show()
                    } else {
                        // 성공적으로  비밀번호 변경할 때
                        if (newPw1.getText().toString() == newPw2.getText().toString()) {
                            builder.setMessage("비밀번호를 변경합니다.")
                            sqlitedb = dbManager.writableDatabase
                            sqlitedb.execSQL(
                                "UPDATE member SET pw = '" + newPw1.getText()
                                    .toString() + "' WHERE id = '"
                                        + USER_ID + "';"
                            )
                            sqlitedb.close()
                            userPw = newPw1.getText().toString()
                            Toast.makeText(
                                getContext(),
                                "'" + newPw1.getText().toString() + "'",
                                Toast.LENGTH_LONG
                            ).show()


                        } else {
                            //변경할 pw가 확인 pw와 다를때
                            builder.setMessage("새로운 비밀번호가 일치하지 않습니다.")
                            //Toast.makeText(getContext(), "'" + newPw1.getText().toString() + "'", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }


            // editText 에  입력한 값 지우기
            currentPw.setText(null);
            newPw1.setText(null);
            newPw2.setText(null);

            //팝업창
            builder.setNeutralButton("확인", null)
            builder.show()


        }

        return preView
    }
}