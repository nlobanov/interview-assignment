package dev.lobanov.raynetassignment.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "features.upload")
data class CsvUploadProperties(
    val defaultRegion: String,
    val batchSize: Int
)