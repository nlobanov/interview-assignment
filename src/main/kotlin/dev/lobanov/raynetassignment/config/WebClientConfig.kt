package dev.lobanov.raynetassignment.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    @Value("\${rcrm.base-url}")
    private val baseUrl: String,
    @Value("\${rcrm.api-key}")
    private val apiKey: String,
    @Value("\${rcrm.instance-name}")
    private val instanceName: String
) {
    @Bean
    fun rcrmWebClient(): WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("X-Api-Key", apiKey)
        .defaultHeader("X-Instance-Name", instanceName)
        .build()
}