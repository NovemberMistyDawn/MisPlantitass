package com.example.mis_plantitass.data

class MyPlant (
    var id: Long,
    val common_name: String,
    var regada: Boolean
){

    companion object {
        const val TABLE_NAME = "Tasks"

        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_REGADA = "regada"
    }
}