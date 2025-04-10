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
       @Query("page") page: Int = 1,
        @Query("key") apiKey: String = "sk-sCUk67f500f7c4bc19647"
    ): PlantResponse

    // Obtener planta por ID
    @GET("species/details/{id}")
    suspend fun findPlantById(@Path("id") id: Int): Plant
}