package dev.lobanov.raynetassignment.web.dto

data class UploadResponse(
    val successCount: Int,
    val failureCount: Int,
    val duplicateCount: Int,
    val totalCount: Int,
    val errors: Map<Int, String>
)