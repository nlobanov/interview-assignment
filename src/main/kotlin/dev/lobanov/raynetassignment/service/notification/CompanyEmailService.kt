package dev.lobanov.raynetassignment.service.notification

import dev.lobanov.raynetassignment.config.UpsertProperties
import dev.lobanov.raynetassignment.service.upsert.UpsertStatistics
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.time.Instant

private val logger = KotlinLogging.logger {}

/**
 * Service for sending email notifications about company.
 */
@Service
class CompanyEmailService(
    private val mailSender: JavaMailSender,
    private val upsertProperties: UpsertProperties,
    @Value("\${spring.mail.username}")
    private val fromEmail: String
) {
    private fun formatUpsertStatistics(upsertStats: UpsertStatistics): String {
        return """
            Company Upsert Process Results
            
            Total companies processed: ${upsertStats.totalProcessed}
            Successfully upserted: ${upsertStats.successful}
            Failed to upsert: ${upsertStats.failed}
            
            Process completed at: ${Instant.now()}
        """.trimIndent()
    }

    fun sendUpsertReport(upsertStats: UpsertStatistics) {
        try {
            val message = SimpleMailMessage().apply {
                from = fromEmail
                setTo(upsertProperties.notificationEmail)
                subject = "Company Upsert Report"
                text = formatUpsertStatistics(upsertStats)
            }

            mailSender.send(message)
            logger.info { "Sent upsert completion email for ${upsertStats.totalProcessed} companies" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to send upsert completion email" }
        }
    }
}