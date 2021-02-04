package com.example.guru2_contestapp

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat

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
    lateinit var wishOnBtn: ImageButton
    lateinit var wishOffBtn: ImageButton

    lateinit var str_name: String
    lateinit var str_host: String
    lateinit var str_start: String
    lateinit var str_end: String
    lateinit var str_section: String
    lateinit var str_detail: String
    lateinit var str_address: String
    var colorSwitch=0 //white

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
        wishOnBtn=findViewById(R.id.wishOnButton)
        wishOffBtn=findViewById(R.id.wishOffButton)

        // 팀 목록에서 팀을 선택하면 그 팀이 참가하는 공모전 번호가 intent로 넘어온다.
        // 그 값으로 DB에서 공모전 이름, 주최기관 등 자세한 정보를 가져온다.
        val intent=intent
        val c_num=intent.getIntExtra("intent_c_num", 0)

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        val cursor: Cursor
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


        // 홈페이지 바로가기 클릭하면 DB에서 가져온 홈페이지 주소로 웹 브라우저를 이용해 이동한다.
        homepage.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(str_address)))
        }


        // 팀 추가 버튼(FloatingActionButton) 클릭하면 팀 생성 페이지로 이동
        // 이동 시 현재 공모전 이름을 intent로 넘김 --> 다음 페이지에서 공모전이 자동으로 선택되게 함
        teamAddBtn.setOnClickListener {
            val intent= Intent(this, BuildTeamActivity::class.java)
            intent.putExtra("intent_c_name", str_name)
            startActivity(intent)
        }

        wishOnBtn.setOnClickListener {
            wishOnBtn.visibility= View.INVISIBLE
            wishOffBtn.visibility= View.VISIBLE

            val id="id later"

            dbManager = DBManager(this, "ContestAppDB", null, 1)
            sqlitedb = dbManager.readableDatabase
            sqlitedb.execSQL("INSERT INTO wish VALUES ('" + id + "'," + c_num+ ")")
            sqlitedb.close()
            dbManager.close()

        }

        wishOffBtn.setOnClickListener {
            wishOnBtn.visibility= View.VISIBLE
            wishOffBtn.visibility= View.INVISIBLE
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