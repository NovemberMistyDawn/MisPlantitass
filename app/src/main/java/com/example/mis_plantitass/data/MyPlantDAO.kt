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
        val db = databaseManager.writableDatabase
        val values = ContentValues().apply {
            put(MyPlant.COLUMN_NAME_TITLE, myplant.common_name)
            put(MyPlant.COLUMN_NAME_REGADA, if (myplant.regada) 1 else 0)
        }

        try {
            val newRowId = db.insert(MyPlant.TABLE_NAME, null, values)
            Log.i("DATABASE", "Inserted plant with id: $newRowId")
            myplant.id = newRowId  // Asegúrate de que el id se actualiza después de la inserción
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
            put(MyPlant.COLUMN_NAME_REGADA, if (plant.regada) 1 else 0)
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
                val regada = cursor.getInt(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_REGADA)) == 1

                plant = MyPlant(id, title,regada)
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
                val regada = cursor.getInt(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_REGADA)) != 0

                val plant = MyPlant(id, title,regada)
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