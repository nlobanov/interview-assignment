package dev.lobanov.raynetassignment.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.Instant
import java.util.*

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class JpaConfig {
    @Bean
    fun auditingDateTimeProvider(): DateTimeProvider = DateTimeProvider {
        Optional.of(Instant.now())
    }
}