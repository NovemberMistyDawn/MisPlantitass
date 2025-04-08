package com.example.mis_plantitass.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.mis_plantitass.data.MyPlant

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
companion object{
    const val DATABASE_NAME = "myplants.db"
    const val DATABASE_VERSION = 1

    private const val SQL_CREATE_TABLE_MYPLANTS =
        "CREATE TABLE ${MyPlant.TABLE_NAME} (" +
                "${MyPlant.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${MyPlant.COLUMN_NAME_TITLE} TEXT," +
                "${MyPlant.COLUMN_NAME_REGADA} BOOLEAN)"

    private const val SQL_DROP_TABLE_TASK = "DROP TABLE IF EXISTS ${MyPlant.TABLE_NAME}"

}

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_MYPLANTS)
        Log.i("DATABASE", "Created table Tasks")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onDestroy(db)
        onCreate(db)
    }

    private fun onDestroy(db: SQLiteDatabase) {
        db.execSQL(com.example.mis_plantitass.utils.DatabaseManager.Companion.SQL_DROP_TABLE_TASK)
    }
}

