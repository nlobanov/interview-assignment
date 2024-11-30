package dev.lobanov.raynetassignment.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.processing")
data class ProcessingProperties(
    val batchSize: Int = 1000,
    val maxFileSize: Long = 10_000_000,
    val retryAttempts: Int = 3,
    val retryDelay: Long = 1000,
    val emailNotification: String? = null
)