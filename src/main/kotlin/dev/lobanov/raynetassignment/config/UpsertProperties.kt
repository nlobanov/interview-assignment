package dev.lobanov.raynetassignment.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "features.upsert")
data class UpsertProperties(
    val maxCompanyLimit: Int,
    val chunkSize: Int,
    val pageSize: Int,
    val notificationEmail: String
)