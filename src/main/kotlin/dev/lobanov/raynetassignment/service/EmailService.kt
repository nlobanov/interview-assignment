package dev.lobanov.raynetassignment.service

import dev.lobanov.raynetassignment.config.ProcessingProperties
import dev.lobanov.raynetassignment.service.processing.UploadStats
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val processingProperties: ProcessingProperties
) {
    fun sendProcessingComplete(result: UploadStats) {
        processingProperties.emailNotification?.let { recipient ->
            val message = SimpleMailMessage().apply {
                setTo(recipient)
                setSubject("CSV Processing Complete")
                setText(
                    """
                    Processing completed:
                    Successful records: ${result.successCount}
                    Failed records: ${result.failureCount}
                """.trimIndent()
                )
            }
            mailSender.send(message)
        }
    }
}