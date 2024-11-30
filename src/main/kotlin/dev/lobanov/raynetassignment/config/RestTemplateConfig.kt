package dev.lobanov.raynetassignment.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate

@EnableConfigurationProperties(UpsertProperties::class, RcrmProperties::class)
@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        val factory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(5000)
            setReadTimeout(30000)
        }

        return RestTemplate(factory).apply {
            errorHandler = DefaultResponseErrorHandler()
        }
    }
}