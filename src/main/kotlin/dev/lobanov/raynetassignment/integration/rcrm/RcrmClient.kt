package dev.lobanov.raynetassignment.integration.rcrm

import dev.lobanov.raynetassignment.dto.RcrmResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class RcrmClient(
    private val webClient: WebClient,
    @Value("\${rcrm.base-url}")
    private val baseUrl: String,
    @Value("\${rcrm.api-key}")
    private val apiKey: String,
    @Value("\${rcrm.instance-name}")
    private val instanceName: String
) {
    suspend fun upsertClient(client: WebClient): RcrmResponse =
        webClient.put()
            .uri("${baseUrl}/clients/")
            .header("X-Api-Key", apiKey)
            .bodyValue(client)
            .retrieve()
            .awaitBody()
}