package com.example.guru2_contestapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        // comment 테이블
        db!!.execSQL("CREATE TABLE IF NOT EXISTS comment(" +
                "cm_idx INTEGER NOT NULL," +
                "t_num INTEGER NOT NULL," +
                "m_id TEXT NOT NULL," +
                "cm_date TEXT NOT NULL," +
                "cm_detail TEXT NOT NULL," +
                "FOREIGN KEY(m_id) REFERENCES member(m_id) ON DELETE CASCADE," +
                "PRIMARY KEY(cm_idx AUTOINCREMENT)" +
                ");")

        // contest 테이블
        db!!.execSQL("CREATE TABLE IF NOT EXISTS contest (" +
                "c_num INTEGER NOT NULL," +
                "c_name TEXT NOT NULL," +
                " c_photo TEXT," +
                "c_host TEXT NOT NULL," +
                "c_section TEXT NOT NULL," +
                "c_start TEXT NOT NULL," +
                "c_end TEXT NOT NULL," +
                "c_address TEXT NOT NULL," +
                "c_detail TEXT," +
                "PRIMARY KEY(c_num AUTOINCREMENT)" +
                ");")

        // member 테이블
        db!!.execSQL("CREATE TABLE member (" +
                "m_name text NOT NULL," +
                "m_id TEXT NOT NULL," +
                "m_pw TEXT NOT NULL," +
                "m_profile BLOB," +
                "m_tel TEXT NOT NULL," +
                "m_year TEXT NOT NULL," +
                "m_month TEXT NOT NULL," +
                "m_date TEXT NOT NULL," +
                "m_email TEXT NOT NULL," +
                "m_job TEXT NOT NULL," +
                "m_univ TEXT," +
                "m_area TEXT NOT NULL," +
                "m_interest TEXT NOT NULL," +
                "PRIMARY KEY(m_id)" +
                ");")

        // resume 테이블
        db!!.execSQL("CREATE TABLE IF NOT EXISTS resume (" +
                "r_idx INTEGER NOT NULL," +
                "m_id TEXT NOT NULL," +
                "t_num INTEGER NOT NULL," +
                "r_hope TEXT NOT NULL," +
                "r_self_intro TEXT NOT NULL," +
                "r_etc TEXT," +
                "FOREIGN KEY(m_id) REFERENCES member(m_id) ON DELETE CASCADE," +
                "PRIMARY KEY(r_idx)" +
                ");")

        // team 테이블
        db!!.execSQL("CREATE TABLE IF NOT EXISTS team (" +
                "t_num INTEGER NOT NULL," +
                "c_num INTEGER NOT NULL," +
                "t_name TEXT NOT NULL," +
                "t_host TEXT NOT NULL," +
                "t_total_num INTEGER NOT NULL," +
                "t_now_num INTEGER," +
                "t_end_date TEXT NOT NULL," +
                "t_need_part TEXT NOT NULL," +
                "t_detail TEXT NOT NULL," +
                "t_complete INTEGER NOT NULL" +
                "FOREIGN KEY(t_host) REFERENCES member(m_id) ON DELETE CASCADE," +
                "PRIMARY KEY(t_num)" +
                ");")

        // teamManage 테이블
        db!!.execSQL("CREATE TABLE teamManage (" +
                "tm_idx INTEGER NOT NULL," +
                "m_id TEXT NOT NULL," +
                "t_num INTEGER NOT NULL," +
                "state INTEGER NOT NULL," +
                "FOREIGN KEY(m_id) REFERENCES member(m_id) ON DELETE CASCADE," +
                "FOREIGN KEY(t_num) REFERENCES team(t_num) ON UPDATE CASCADE," +
                "PRIMARY KEY(tm_idx)" +
                ");")

        // wishlist 테이블
        db!!.execSQL("CREATE TABLE wishlist (" +
                "w_idx INTEGER NOT NULL," +
                "m_id TEXT NOT NULL," +
                "c_num INTEGER NOT NULL," +
                "state INTEGER NOT NULL," +
                "PRIMARY KEY(w_idx)," +
                "FOREIGN KEY(m_id) REFERENCES member(m_id) ON DELETE CASCADE," +
                "FOREIGN KEY(c_num) REFERENCES contest(c_num) ON UPDATE CASCADE" +
                ");")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}