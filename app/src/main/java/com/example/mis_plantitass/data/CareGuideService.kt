package com.example.mis_plantitass.data


import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface CareGuideService {

    @GET("species-care-guide-list")
    suspend fun getCareGuideBySpeciesId(
        @Query("key") apiKey: String,
        @Query("species_id") speciesId: Int
    ): CareGuideResponse
}