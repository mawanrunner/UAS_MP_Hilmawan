package com.uas_project.uas_alfares_2107411020

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context):SQLiteOpenHelper(context, "Userdata", null, 1) {
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("create table Userdata (username TEXT, password TEXT)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("drop table if exists Userdata")
    }

    fun insertData(username: String, password: String): Boolean {
        val p0 = this.writableDatabase
        val cv = ContentValues()
        cv.put("username",username)
        cv.put("password",password)

        val query = "select * from Userdata where username='$username' and password='$password'"
        val cursor = p0.rawQuery(query, null)
        val result = p0.insert("Userdata", null, cv)

        if (result == (-1).toLong() || cursor.count > 0){
            cursor.close()
            return false
        }
        return true
    }

    fun checkUserPass(username: String, password: String): Boolean {
        val p0 = this.writableDatabase
        val query = "select * from Userdata where username='$username' and password='$password'"
        val cursor = p0.rawQuery(query, null)
        if (cursor.count == 0){
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

}