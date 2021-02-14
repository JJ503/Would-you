package com.example.guru2_contestapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class ChangePwFragment : Fragment() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        var preView = inflater.inflate(R.layout.fragment_change_pw, container, false)

        var currentPw: EditText = preView.findViewById(R.id.currentPw)
        var newPw1: EditText = preView.findViewById(R.id.newPw1)
        var newPw2: EditText = preView.findViewById(R.id.newPw2)
        var changePwBtn: Button = preView.findViewById(R.id.changePwBtn)


        //현재 로그인 중인 사용자 지정
        var context: Context = requireContext()
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")


        // 사용자의 pw를 db에서 가져온다.
        var USER_PW: String=""

        dbManager = DBManager(activity, "ContestAppDB", null, 1)
        sqlitedb =dbManager.readableDatabase

        try {
            if (sqlitedb != null) {
                var cursor: Cursor
                cursor = sqlitedb.rawQuery("SELECT m_pw FROM member WHERE m_id = '" + USER_ID + "';", null)
                if (cursor.getCount() != 0) {
                    if (cursor.moveToNext()) {
                        USER_PW = cursor.getString(cursor.getColumnIndex("m_pw"))
                    }
                }
                cursor.close()
            }
        }catch (e: Exception){
            Log.e("Error", e.message.toString())
        } finally{
            sqlitedb.close()
            dbManager.close()
        }


        // 버튼 클릭시 팝업창 열기
        changePwBtn.setOnClickListener {

            val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)


            val builder = AlertDialog.Builder(activity)
            builder.setTitle("비밀번호 변경")
            builder.setIcon(R.drawable.logo_2_04)

            // '현재 비밀번호' 입력 안 했을 때
            if (currentPw.getText().toString().equals("") || currentPw.getText().toString() == null) {
                builder.setMessage("현재 비밀번호를 입력하세요.")
            } else {
                // '현재 비밀번호'가 아닐 때
                if (USER_PW != currentPw.getText().toString()) {
                    builder.setMessage("현재 비밀번호가 아닙니다.")
                } else {
                     // '변경할 비밀번호'를 입력하지 않았을 때
                    if (newPw1.getText().toString().equals("") || newPw1.getText().toString() == null || newPw2.getText().toString().equals("") || newPw2.getText().toString() == null
                    ) {
                        builder.setMessage("변경할 비밀번호를 입력하세요. (확인란도 작성)")
                    } else {
                        // 비밀번호 변경
                        if (newPw1.getText().toString() == newPw2.getText().toString()) {
                            builder.setMessage("비밀번호를 변경합니다.")
                            sqlitedb = dbManager.writableDatabase
                            sqlitedb.execSQL("UPDATE member SET m_pw = '" + newPw1.getText().toString() + "' WHERE m_id = '" + USER_ID + "';")
                            sqlitedb.close()

                            USER_PW = newPw1.getText().toString()
                        } else {
                            //변경할 pw가 확인 pw와 다를때
                            builder.setMessage("새로운 비밀번호가 일치하지 않습니다.")
                        }
                    }
                }
            }

            // editText에  입력한 값 지우기
            currentPw.setText(null);
            newPw1.setText(null);
            newPw2.setText(null);

            //팝업창
            builder.setPositiveButton("확인", null)
            builder.show()
        }

        return preView
    }

}
