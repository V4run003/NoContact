package com.nxet.nocontact.Helpers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create Table Configstable (id TEXT primary key)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop Table if exists Configstable")
    }

    fun delete() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM Configstable") //delete all rows in a table
        db.close()
    }

    fun insertdata(
        id: String
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id", id)
        val result = db.insert("Configstable", null, contentValues)
        return result != -1L
    }


    @SuppressLint("Recycle")
    fun deletedata(id: String): Boolean {
        val db = this.writableDatabase
        val cursor = db.rawQuery("Select * from Configstable where id = ?", arrayOf(id))
        return if (cursor.count > 0) {
            val result = db.delete("Configstable", "id=?", arrayOf(id)).toLong()
            result != -1L
        } else {
            false
        }
    }

    fun getdata(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("Select * from Configstable", null)
    }

    fun checkAlreadyExist(id: String): Boolean {
        val db = this.writableDatabase
        val query = "SELECT id FROM Configstable WHERE id  =?"
        val cursor = db.rawQuery(query, arrayOf(id))
        return cursor.count <= 0
    }

    fun getitemvalues(id: String): Cursor {
        val cursor: Cursor
        val db = this.writableDatabase
        val query = "SELECT * FROM Configstable WHERE id  =?"
        cursor = db.rawQuery(query, arrayOf(id))
        return cursor
    }

    @Synchronized
    override fun close() {
        val db = this.writableDatabase
        if (db != null) {
            db.close()
            super.close()
        }
    }

    companion object {
        const val DATABASE_NAME = "configs.db"
    }
}
