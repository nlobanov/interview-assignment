package dev.lobanov.raynetassignment.service.processing

class UploadStats {
    var successCount = 0
    var failureCount = 0
    val errors = mutableMapOf<Int, String>()

    fun recordSuccess() {
        successCount++
    }

    fun recordError(lineNumber: Int, message: String) {
        failureCount++
        errors[lineNumber] = message
    }
}