package dev.lobanov.raynetassignment.service.upload

import dev.lobanov.raynetassignment.config.CsvUploadProperties
import dev.lobanov.raynetassignment.model.Company
import dev.lobanov.raynetassignment.repository.CompanyRepository
import dev.lobanov.raynetassignment.utils.validation.CsvValidator
import dev.lobanov.raynetassignment.web.dto.UploadResponse
import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.io.InputStream

private val logger = KotlinLogging.logger {}

/**
 * Service for uploading CSV files with company data.
 */
@Service
class CompanyUploadService(
    private val repository: CompanyRepository,
    private val props: CsvUploadProperties,
    private val transactionTemplate: TransactionTemplate
) {
    private val validator = CsvValidator()
    private val mapper = CompanyMapper()

    fun uploadFromCsv(inputStream: InputStream): UploadResponse {
        logger.info { "Starting CSV upload..." }
        val stats = UploadStatistics()
        val currentBatch = mutableMapOf<String, Company>()

        try {
            inputStream.bufferedReader().use { reader ->
                val parser = CSVFormat.Builder.create()
                    .setDelimiter(';')
                    .setHeader()
                    .build()
                    .parse(reader)

                validator.validateHeaders(parser.headerNames)
                processContent(parser, currentBatch, stats)

                if (currentBatch.isNotEmpty()) {
                    saveBatch(currentBatch.values.toList(), parser.records.size - currentBatch.size, stats)
                    currentBatch.clear()
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "CSV uploading failed" }
            stats.recordError(0, "Failed to upload CSV: ${e.message}")
        }

        logger.info { "Completed the upload process for ${stats.totalCount} CSV records" }
        return stats.toUploadResponse()
    }

    // Validates CSV records, processes them into company entities, and saves in batches
    private fun processContent(
        parser: CSVParser,
        currentBatch: MutableMap<String, Company>,
        stats: UploadStatistics
    ) {
        parser.forEachIndexed { index, csvRecord ->
            val line = index + 2
            try {
                val companyRecord = mapper.toCompanyRecord(csvRecord)
                val validationResult = validator.validateRecord(companyRecord, props.defaultRegion)

                if (validationResult.isValid) {
                    if (currentBatch.containsKey(companyRecord.regNumber)) {
                        stats.incrementDuplicates()
                    }

                    currentBatch[companyRecord.regNumber] = mapper.toEntity(companyRecord)

                    if (currentBatch.size >= props.batchSize) {
                        saveBatch(currentBatch.values.toList(), line - currentBatch.size, stats)
                        currentBatch.clear()
                    }
                } else {
                    stats.recordError(line, validationResult.errors.joinToString("; "))
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to upload csvRecord at line $line" }
                stats.recordError(line, "uploading failed: ${e.message}")
            }
        }
    }

    private fun saveBatch(batch: List<Company>, startLine: Int, stats: UploadStatistics) {
        try {
            transactionTemplate.execute {
                repository.saveAll(batch)
                stats.incrementSuccess(batch.size)
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to save batch of ${batch.size} records" }
            batch.forEachIndexed { index, _ ->
                stats.recordError(
                    startLine + index,
                    "Batch save failed for lines $startLine - ${startLine + batch.size}: ${e.message}"
                )
            }
        }
    }
}