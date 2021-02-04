package com.cookandroid.guru2_joinus

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) { //공모전 테이블 생성
        db!!.execSQL("CREATE TABLE member (name text not NULL, id TEXT NOT NULL UNIQUE, pw TEXT NOT NULL, profile BLOB, tel TEXT NOT NULL, year INT NOT NULL, month  int NOT NULL, date int NOT NULL, email TEXT NOT NULL, job TEXT NOT NULL, univ TEXT, area TEXT NOT NULL, interest TEXT NOT NULL, PRIMARY KEY(id))")

    }
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}