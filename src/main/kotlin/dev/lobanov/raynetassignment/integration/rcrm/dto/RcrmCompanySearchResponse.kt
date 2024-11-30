package dev.lobanov.raynetassignment.integration.rcrm.dto

data class RcrmCompanySearchResponse(
    val success: Boolean,
    val totalCount: Int,
    val data: List<RcrmCompanyId>
)