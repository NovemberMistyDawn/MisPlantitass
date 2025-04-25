package com.example.mis_plantitass.data


class PlantResponse (

    val data: List<Plant>   // Lista de superhéroes obtenida desde la API.
)


class Plant (
    val id: Int,
    val common_name: String?,
    val scientific_name: List<String>?,  // Cambiado de String a List<String> porque en el JSON es un array
    val other_name: List<String>?,
    val default_image: Image?,
    val care_guide: List<CareSection>,
    val type: String?,  // <-- AÑADIR
    val maintenance: String?,  // <-- AÑADIR
    val indoor: Boolean?,  // <-- opcional si luego filtras por eso
    val cycle: String?,  // (Perennial, Annual, etc.)
    val watering: String?,  // (Frequent, Average...)
    val sunlight: List<String>?,  // ["full sun", "part shade

)

class Image(
val image_id: Int?,
val license: Int?,
val license_name: String,
val license_url: String,
val original_url: String,
val regular_url: String,
val medium_url: String,
val small_url: String,
val thumbnail: String
)