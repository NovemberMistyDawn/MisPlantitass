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
            put("tienePlagas", if (myplant.tienePlagas) 1 else 0)
            if (myplant.ultimaFechaRiego > 0L) {
                put("ultimaFechaRiego", myplant.ultimaFechaRiego)
            } else {
                putNull("ultimaFechaRiego")
            }
        }

        try {
            val newRowId = db.insert(MyPlant.TABLE_NAME, null, values)
            Log.i("DATABASE", "Inserted plant with id: $newRowId")
            myplant.id = newRowId
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun update(plant: MyPlant) {
        val db = databaseManager.writableDatabase
        val values = ContentValues().apply {
            put(MyPlant.COLUMN_NAME_TITLE, plant.common_name)
            put(MyPlant.COLUMN_NAME_REGADA, if (plant.regada) 1 else 0)
            put("tienePlagas", if (plant.tienePlagas) 1 else 0)
            put("ultimaFechaRiego", if (plant.ultimaFechaRiego > 0L) plant.ultimaFechaRiego else null)
        }

        try {
            db.update(MyPlant.TABLE_NAME, values, "${MyPlant.COLUMN_NAME_ID} = ?", arrayOf(plant.id.toString()))
            Log.i("DATABASE", "Updated plant with id: ${plant.id}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun delete(plant: MyPlant) {
        val db = databaseManager.writableDatabase

        try {
            db.delete(MyPlant.TABLE_NAME, "${MyPlant.COLUMN_NAME_ID} = ?", arrayOf(plant.id.toString()))
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
            MyPlant.COLUMN_NAME_REGADA,
            "tienePlagas",
            "ultimaFechaRiego"
        )

        val selection = "${MyPlant.COLUMN_NAME_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        var plant: MyPlant? = null

        try {
            val cursor = db.query(
                MyPlant.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            if (cursor.moveToNext()) {
                val plantId = cursor.getLong(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_TITLE))
                val regada = cursor.getInt(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_REGADA)) == 1
                val tienePlagas = cursor.getInt(cursor.getColumnIndexOrThrow("tienePlagas")) == 1
                val ultimaFechaIndex = cursor.getColumnIndexOrThrow("ultimaFechaRiego")
                val ultimaFechaRiego =
                    if (cursor.isNull(ultimaFechaIndex)) 0L else cursor.getLong(ultimaFechaIndex)
                plant = MyPlant(plantId, title, regada, tienePlagas, ultimaFechaRiego)
            }
            cursor.close()
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
            MyPlant.COLUMN_NAME_REGADA,
            "tienePlagas",
            "ultimaFechaRiego"
        )

        val plantList = mutableListOf<MyPlant>()

        try {
            val cursor = db.query(
                MyPlant.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_TITLE))
                val regada = cursor.getInt(cursor.getColumnIndexOrThrow(MyPlant.COLUMN_NAME_REGADA)) == 1
                val tienePlagas = cursor.getInt(cursor.getColumnIndexOrThrow("tienePlagas")) == 1
                val ultimaFechaIndex = cursor.getColumnIndexOrThrow("ultimaFechaRiego")
                val ultimaFechaRiego =
                    if (cursor.isNull(ultimaFechaIndex)) 0L else cursor.getLong(ultimaFechaIndex)

                val plant = MyPlant(id, title, regada, tienePlagas, ultimaFechaRiego)
                plantList.add(plant)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return plantList
    }
}