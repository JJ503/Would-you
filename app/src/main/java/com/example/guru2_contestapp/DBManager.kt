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
        db!!.execSQL("CREATE TABLE IF NOT EXISTS contest(c_num INTEGER, c_name TEXT, c_photo TEXT, c_host TEXT, c_section TEXT, c_start TEXT, c_end TEXT, c_address TEXT, c_detail TEXT, PRIMARY KEY(c_num))")
        db!!.execSQL("CREATE TABLE IF NOT EXISTS team (t_num INTEGER, c_num INTEGER, t_name TEXT, t_total_num INTEGER, t_now_num INTEGER, t_endDate TEXT, t_need_part TEXT, t_detail TEXT, PRIMARY KEY(t_num))")
        db!!.execSQL("CREATE TABLE IF NOT EXISTS comment(cm_idx INTEGER, t_num INTEGER, s_id TEXT, cm_date TEXT, cm_detail TEXT, PRIMARY KEY(cm_idx))")
        db!!.execSQL("CREATE TABLE IF NOT EXISTS resume (r_idx INTEGER, s_id TEXT, t_num INTEGER, r_hope TEXT, r_self_intro TEXT, r_etc TEXT, PRIMARY KEY(r_idx))")

        db!!.execSQL("CREATE TABLE teamManage (" +
                "id TEXT NOT NULL," +
                "t_num INTEGER," +
                "state INTEGER NOT NULL," +
                "FOREIGN KEY(id) REFERENCES member(id) ON DELETE CASCADE," +
                "FOREIGN KEY(t_num) REFERENCES team(t_num) ON UPDATE CASCADE" +
                ");")

        db!!.execSQL("CREATE TABLE wishlist (" +
                "id TEXT NOT NULL," +
                "t_num INTEGER," +
                "state	INTEGER," +
                "FOREIGN KEY(id) REFERENCES member(id) ON DELETE CASCADE," +
                "FOREIGN KEY(t_num) REFERENCES team(t_num) ON UPDATE CASCADE" +
                ");")

        db!!.execSQL("CREATE TABLE member (" +
                "name TEXT NOT NULL," +
                "id TEXT NOT NULL UNIQUE," +
                "pw TEXT NOT NULL," +
                "profile BLOB," +
                "tel TEXT NOT NULL," +
                "year TEXT NOT NULL," +
                "month TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "job TEXT NOT NULL," +
                "univ TEXT," +
                "area TEXT NOT NULL," +
                "interest TEXT NOT NULL," +
                "PRIMARY KEY(id)" +
                ");")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}