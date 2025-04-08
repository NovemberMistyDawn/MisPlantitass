package com.example.mis_plantitass.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.mis_plantitass.utils.DatabaseManager
import kotlin.text.insert

class MyPlantDAO(private val databaseManager: DatabaseManager) {



    fun insert(myplant: MyPlant) {
        // Gets the data repository in write mode
        val db = databaseManager.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(MyPlant.COLUMN_NAME_TITLE, myplant.common_name)
            //put(MyPlant.COLUMN_NAME_REGADA, myplant.done)
        }

        try {
            // Insert the new row, returning the primary key value of the new row
            val newRowId = db.insert(MyPlant.TABLE_NAME, null, values)

            Log.i("DATABASE", "Inserted plant with id: $newRowId")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun update(plant: MyPlant) {
        // Gets the data repository in write mode
        val db = databaseManager.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(MyPlant.COLUMN_NAME_TITLE, plant.common_name)
            //put(Task.COLUMN_NAME_DONE, task.done)
        }

        try {
            val updatedRows = db.update(MyPlant.TABLE_NAME, values, "${MyPlant.COLUMN_NAME_ID} = ${plant.id}", null)

            Log.i("DATABASE", "Updated task with id: ${plant.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }



    fun delete(plant: MyPlant) {
        val db = databaseManager.writableDatabase

        try {
            val deletedRows = db.delete(MyPlant.TABLE_NAME, "${MyPlant.COLUMN_NAME_ID} = ${plant.id}", null)

            Log.i("DATABASE", "Deleted plant with id: ${plant.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun findById(id: Long): MyPlant? {
        val db = databaseManager.readableDatabase

        val projection = arrayOf(
            MyPlant.COLUMN_NAME_ID,
            MyPlant.COLUMN_NAME_TITLE,
            MyPlant.COLUMN_NAME_REGADA
        )

        val selection = "${MyPlant.COLUMN_NAME_ID} = $id"

        var plant: MyPlant? = null

        try {
            val cursor = db.query(
                MyPlant.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            if (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_TITLE))


                plant = MyPlant(id, title)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return plant
    }

    fun findAll(): List<MyPlant> {
        val db = databaseManager.readableDatabase

        val projection = arrayOf(
            MyPlant.COLUMN_NAME_ID,
            MyPlant.COLUMN_NAME_TITLE,
            MyPlant.COLUMN_NAME_REGADA
        )

        var plantList: MutableList<MyPlant> = mutableListOf()

        try {
            val cursor = db.query(
                MyPlant.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_TITLE))
                val done = cursor.getInt(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_REGADA)) != 0

                val plant = MyPlant(id, title)
                plantList.add(plant)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return plantList
    }

}