package com.example.guru2_contestapp

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import java.lang.Exception


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
    lateinit var contestImg: ImageView

    lateinit var str_photo: String
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

        supportActionBar?.elevation = 3f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        supportActionBar?.title = Html.fromHtml("<font color=\"#000000\">" + getString(R.string.action_contestDetail)+"</font>")

        // 로그인한 계정 아이디
        val sharedPreferences : SharedPreferences = this.getSharedPreferences("userid", AppCompatActivity.MODE_PRIVATE)
        var USER_ID = sharedPreferences.getString("USER_ID", "sorry")

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
        contestImg=findViewById(R.id.WposterImageView)

        // 팀 목록에서 팀을 선택하면 그 팀이 참가하는 공모전 번호가 intent로 넘어온다.
        // 그 값으로 DB에서 공모전 이름, 주최기관 등 자세한 정보를 가져온다.
        val intent=intent
        val c_num=intent.getIntExtra("intent_c_num", 0)

        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor
        try {
            if(sqlitedb!=null){
                cursor=sqlitedb.rawQuery("SELECT * FROM contest WHERE c_num = '"+c_num+"';", null)

                if(cursor.count!=0){
                    if(cursor.moveToNext()){
                        str_photo=cursor.getString(cursor.getColumnIndex("c_photo"))
                        str_name=cursor.getString(cursor.getColumnIndex("c_name"))
                        str_host=cursor.getString(cursor.getColumnIndex("c_host"))
                        str_section=cursor.getString(cursor.getColumnIndex("c_section"))
                        str_start=cursor.getString(cursor.getColumnIndex("c_start"))
                        str_end=cursor.getString(cursor.getColumnIndex("c_end"))
                        str_detail=cursor.getString(cursor.getColumnIndex("c_detail"))
                        str_address=cursor.getString(cursor.getColumnIndex("c_address"))
                    }
                }
                cursor.close()
            }
        } catch(e: Exception){
            Log.e("Error", e.message.toString())
        } finally {
            sqlitedb.close()
            dbManager.close()
        }

        var photo_src=this.resources.getIdentifier(str_photo,"drawable", "com.example.guru2_contestapp")
        contestImg.setImageResource(photo_src)
        contestName.text = str_name
        hostName.text = str_host
        section.text= str_section
        start.text = str_start
        end.text = str_end
        detail.text= str_detail


        // 현재 로그인 된 계정에 대한 wishlist 정보를 가져와 해당 공모전에 state가 1이면 노란별,
        // 0이면 빈 별을 보여준다.
        dbManager = DBManager(this, "ContestAppDB", null, 1)
        sqlitedb = dbManager.readableDatabase
        try{
            if(sqlitedb!=null){
                cursor=sqlitedb.rawQuery("SELECT state FROM wishlist WHERE m_id = '"+USER_ID+"' AND c_num = "+c_num+";", null)

                if(cursor.count!=0){
                    var state=-1 //아무상태 아님
                    if(cursor.moveToNext()){
                        state=cursor.getInt(cursor.getColumnIndex("state"))
                    }

                    when(state){
                        0 -> {  // 빈 상태
                            wishOnBtn.visibility= View.INVISIBLE
                            wishOffBtn.visibility= View.VISIBLE
                        }
                        1 -> {  // 좋아요 선택
                            wishOnBtn.visibility= View.VISIBLE
                            wishOffBtn.visibility= View.INVISIBLE
                        }
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

        // 홈페이지 바로가기 클릭하면 DB에서 가져온 홈페이지 주소로 웹 브라우저를 이용해 이동
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


        //빈 별을 클릭한 경우 DB에서 로그인 계정의 현재 공모전에 대한 state를 1로 바꿔주고, 빈 별을 숨기고 노란 별을 보여준다.
        wishOffBtn.setOnClickListener {
            wishOnBtn.visibility= View.VISIBLE
            wishOffBtn.visibility= View.INVISIBLE

            dbManager = DBManager(this, "ContestAppDB", null, 1)
            sqlitedb = dbManager.readableDatabase
            try {
                if(sqlitedb!=null){
                    cursor=sqlitedb.rawQuery("SELECT * FROM wishlist WHERE m_id = '"+USER_ID+"' AND c_num = "+c_num+";", null)
                    if(cursor.count==0){
                        sqlitedb.execSQL("INSERT INTO wishlist (m_id, c_num, state) VALUES ('"+USER_ID+"', "+c_num+", 1)")
                    }else{
                        sqlitedb.execSQL("UPDATE wishlist SET state = 1 WHERE m_id ='"+ USER_ID+"' AND c_num =" +c_num+";")
                    }
                    cursor.close()
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
                dbManager.close()
            }
        }

        //노란별 별을 클릭한 경우 DB에서 로그인 계정의 현재 공모전에 대한 state를 0로 바꿔주고, 노란 별을 숨기고 빈 별을 보여준다.
        wishOnBtn.setOnClickListener {
            wishOnBtn.visibility= View.INVISIBLE
            wishOffBtn.visibility= View.VISIBLE

            dbManager = DBManager(this, "ContestAppDB", null, 1)
            sqlitedb = dbManager.readableDatabase
            try {
                if(sqlitedb!=null){
                    sqlitedb.execSQL("UPDATE wishlist SET state = 0 WHERE m_id ='"+ USER_ID+"' AND c_num =" +c_num+";")
                }
            } catch(e: Exception){
                Log.e("Error", e.message.toString())
            } finally{
                sqlitedb.close()
                dbManager.close()
            }
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