package com.example.mis_plantitass.data


import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlantService {




    // Buscar plantas por nombre usando el endpoint species-list con query
    @GET("species-list")
    suspend fun findPlantsByQuery(
         @Query("q") query: String,
       // @Query("page") page: Int = 1,
        @Query("key") apiKey: String = "sk-aVN467f395a2d696a9647"
    ): PlantResponse

    // Este endpoint parece incorrecto a menos que sea parte de otra API
    // El endpoint /search/{scientific_name} no está en la doc pública de Perenual
    // Lo más probable es que NO funcione o no lo necesites
    // Si no funciona, coméntalo o elimínalo
    /*
    @GET("search/{scientific_name}")
    suspend fun searchByScientificName(@Path("scientific_name") query: String): PlantResponse
    */

    // Obtener planta por ID
    @GET("species/details/{id}")
    suspend fun findPlantById(@Path("id") id: Int): Plant
}