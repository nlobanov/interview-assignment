package dev.lobanov.raynetassignment.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rcrm")
data class RcrmProperties(
    val username: String,
    val apiKey: String,
    val baseUrl: String,
    val instanceName: String
)