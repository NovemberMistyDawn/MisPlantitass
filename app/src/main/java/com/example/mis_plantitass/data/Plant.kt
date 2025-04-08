package com.example.mis_plantitass.data


class PlantResponse (
    val response: String,          // Estado de la respuesta (por ejemplo, "success").
    val results: List<Plant>   // Lista de superhéroes obtenida desde la API.
)


class Plant (
    val id: Int,
    val common_name: String,
    val scientific_name: String,
    val default_image: Image

)

class Image (val url: String)