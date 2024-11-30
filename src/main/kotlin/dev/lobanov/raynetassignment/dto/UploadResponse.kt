package dev.lobanov.raynetassignment.dto

data class UploadResponse(
    val successCount: Int,
    val failureCount: Int,
    val errors: Map<Int, String>
)