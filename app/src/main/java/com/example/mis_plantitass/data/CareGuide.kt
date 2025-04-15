package com.example.mis_plantitass.data

data class CareGuideResponse(
    val data: List<CareGuide>,
    val to: Int,
    val per_page: Int,
    val current_page: Int,
    val last_page: Int,
    val total: Int
)

data class CareGuide(
    val id: Int,
    val species_id: Int,
    val common_name: String,
    val scientific_name: List<String>,
    val section: List<CareSection>
)

data class CareSection(
    val id: Int,
    val type: String,
    val description: String
)