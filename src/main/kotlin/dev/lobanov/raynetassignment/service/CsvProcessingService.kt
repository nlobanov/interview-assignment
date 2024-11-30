package dev.lobanov.raynetassignment.service

import dev.lobanov.raynetassignment.config.ProcessingProperties
import dev.lobanov.raynetassignment.dto.UploadResponse
import dev.lobanov.raynetassignment.model.*
import dev.lobanov.raynetassignment.repository.CompanyRepository
import dev.lobanov.raynetassignment.service.processing.UploadStats
import dev.lobanov.raynetassignment.utils.CsvHeaders
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.io.InputStream

@Service
class CsvProcessingService(
    private val companyRepository: CompanyRepository,
    private val props: ProcessingProperties,
    private val transactionTemplate: TransactionTemplate
) {
    fun process(inputStream: InputStream): UploadResponse {
        val stats = UploadStats()

        inputStream.bufferedReader().use { reader ->
            val parser = CSVFormat.Builder.create()
                .setDelimiter(';')
                .setHeader()
                .build()
                .parse(reader)

            validateHeaders(parser.headerNames)
            processContent(parser, stats)
        }

        return UploadResponse(stats.successCount, stats.failureCount, stats.errors)
    }

    private fun validateHeaders(headers: List<String>) {
        require(headers.containsAll(CsvHeaders.ALL)) { "Invalid headers. Expected: ${CsvHeaders.ALL}" }
    }

    private fun processContent(parser: CSVParser, result: UploadStats) {
        var lineNumber = 1
        val batch = mutableListOf<Company>()

        parser.forEach { record ->
            lineNumber++
            try {
                processSingleRecord(record, batch)
                if (batch.size >= props.batchSize) {
                    saveBatch(batch, lineNumber - batch.size, result)
                    batch.clear()
                }
            } catch (e: Exception) {
                result.failureCount++
                result.errors[lineNumber] = e.message ?: "Unknown error"
            }
        }

        if (batch.isNotEmpty()) {
            saveBatch(batch, lineNumber - batch.size, result)
        }
    }

    private fun processSingleRecord(record: CSVRecord, batch: MutableList<Company>) {
        val company = Company(
            name = record[CsvHeaders.TITLE],
            regNumber = record[CsvHeaders.REG_NUMBER],
            addresses = listOf(
                CompanyAddress(
                    address = Address(
                        name = "Primary Address",
                        country = "CZ"
                    ),
                    contactInfo = ContactInfo(
                        email = record[CsvHeaders.EMAIL],
                        tel1 = record[CsvHeaders.PHONE]
                    ),
                    territory = 1L
                )
            ),
            rating = Rating.C,
            state = CompanyState.A_POTENTIAL,
            role = CompanyRole.A_SUBSCRIBER
        )
        batch.add(company)
    }

    private fun saveBatch(
        batch: List<Company>,
        startLineNumber: Int,
        result: UploadStats
    ) {
        var attempts = 0
        while (attempts < props.retryAttempts) {
            try {
                transactionTemplate.execute {
                    companyRepository.saveAll(batch)
                    result.successCount += batch.size
                }
                return
            } catch (e: Exception) {
                attempts++
                if (attempts == props.retryAttempts) {
                    batch.forEachIndexed { index, _ ->
                        result.failureCount++
                        result.errors[startLineNumber + index] =
                            "Failed to save after ${props.retryAttempts} attempts"
                    }
                } else {
                    Thread.sleep(props.retryDelay * attempts)
                }
            }
        }
    }
}