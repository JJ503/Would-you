package com.example.a0202

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class ContestDetailActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var contestName: TextView
    lateinit var hostName: TextView
    lateinit var teamAddBtn: ImageButton
    lateinit var section: TextView
    lateinit var start: TextView
    lateinit var end: TextView
    lateinit var detail: TextView
    lateinit var homepage: TextView

    lateinit var str_name: String
    lateinit var str_host: String
    lateinit var str_start: String
    lateinit var str_end: String
    lateinit var str_section: String
    lateinit var str_detail: String
    lateinit var str_address: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contest_detail)

        contestName=findViewById(R.id.WDetailContextNameTextView)
        hostName=findViewById(R.id.WDetailHostNameTextView)
        section=findViewById(R.id.WSectionTextView)
        start=findViewById(R.id.WdetailStartDayTextView)
        end=findViewById(R.id.WdetailEndDayTextView)
        detail=findViewById(R.id.WdetailTextView)
        homepage=findViewById(R.id.WcontestLinkTextView)

        teamAddBtn=findViewById(R.id.WsearchTeamButton)

        val intent=intent
        var c_num=intent.getIntExtra("intent_c_num", 0)

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        cursor=sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = '"+c_num+"';", null)

        if(cursor.moveToNext()){
            //var photo
            str_name=cursor.getString(cursor.getColumnIndex("c_name")).toString()
            str_host=cursor.getString(cursor.getColumnIndex("c_host")).toString()
            str_section=cursor.getString(cursor.getColumnIndex("c_section")).toString()
            str_start=cursor.getString(cursor.getColumnIndex("c_start")).toString()
            str_end=cursor.getString(cursor.getColumnIndex("c_end")).toString()
            str_detail=cursor.getString(cursor.getColumnIndex("c_detail")).toString()
            str_address=cursor.getString(cursor.getColumnIndex("c_address")).toString()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        contestName.text = str_name
        hostName.text = str_host
        section.text= str_section
        start.text = str_start
        end.text = str_end
        detail.text= str_detail

        // 홈페이지 바로가기 클릭
        homepage.setOnClickListener {
            Toast.makeText(this, "Aaaaaa", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(str_address)))
        }

        // 팀 추가 버튼
        teamAddBtn.setOnClickListener {
            val intent= Intent(this, MakeTeamActivity::class.java)
            intent.putExtra("intent_c_name", str_name)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}