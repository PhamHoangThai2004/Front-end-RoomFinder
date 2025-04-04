package com.pht.roomfinder.services

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteService(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
    companion object {
        private const val DATABASE_NAME = "notification.db"
        private const val VERSION = 2

        private const val TABLE_NAME = "notifications"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_MESSAGE = "content"
        private const val COLUMN_POST_ID = "postId"
        private const val COLUMN_TIME = "time"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val query =
            """CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_TITLE TEXT,
                    $COLUMN_MESSAGE TEXT, 
                    $COLUMN_POST_ID INTEGER,
                    $COLUMN_TIME TEXT)""".trimIndent()
        p0?.execSQL(query)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(p0)
        Log.d("BBB", "onUpgrade: ")
    }

    fun insertNotification(title: String, message: String, postId: Int, time: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_MESSAGE, message)
            put(COLUMN_POST_ID, postId)
            put(COLUMN_TIME, time)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun getAllNotifications(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC", null)
    }

    fun deleteNotification(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return result != -1
    }

    fun deleteAllNotifications(): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, null, null)
        db.close()
        return result != -1
    }

    fun deleteTable() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.close()
        Log.d("BBB", "deleteTable: ")
    }

    fun getTableColumns(): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("PRAGMA table_info($TABLE_NAME)", null)
        val columns = mutableListOf<String>()

        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex("name")
            do {
                if (nameIndex != -1) {
                    columns.add(cursor.getString(nameIndex))
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return columns
    }


}