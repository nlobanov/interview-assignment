package dev.lobanov.raynetassignment.service.upload

import dev.lobanov.raynetassignment.web.dto.UploadResponse

/**
 * Tracks statistics for CSV file importing.
 * Errors are stored as line number to error message mappings.
 *
 * @property successCount Number of successfully processed records
 * @property duplicateCount Number of duplicate records found (based on regNumber)
 * @property failureCount Number of failed records
 * @property totalCount Total records processed (success + failed + duplicate)
 * @property errors Map of line numbers to error messages
 */
class UploadStatistics {
    var successCount = 0
        private set

    var duplicateCount = 0
        private set

    private val errors = mutableMapOf<Int, String>()

    val failureCount: Int
        get() = errors.size

    val totalCount: Int
        get() = successCount + duplicateCount + failureCount

    fun incrementSuccess(by: Int) {
        require(by > 0) { "Increment value must be positive." }
        successCount += by
    }

    fun incrementDuplicates() {
        duplicateCount++
    }

    fun recordError(lineNumber: Int, message: String) {
        require(lineNumber >= 0) { "Line number must be non-negative." }
        require(message.isNotBlank()) { "Error message cannot be blank." }
        errors[lineNumber] = message
    }

    fun toUploadResponse(): UploadResponse = UploadResponse(
        successCount = successCount,
        failureCount = failureCount,
        duplicateCount = duplicateCount,
        totalCount = totalCount,
        errors = errors
    )
}