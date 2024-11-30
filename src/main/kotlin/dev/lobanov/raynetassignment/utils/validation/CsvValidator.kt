package dev.lobanov.raynetassignment.utils.validation

import dev.lobanov.raynetassignment.service.upload.CompanyRecord
import dev.lobanov.raynetassignment.utils.CsvHeaders
import jakarta.validation.Validation
import jakarta.validation.Validator

/**
 * Validates CSV with companies.
 */
class CsvValidator {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator
    private val phoneValidator = PhoneValidator()

    fun validateHeaders(headers: List<String>) {
        require(headers.containsAll(CsvHeaders.ALL)) { "Invalid CSV headers. Expected: ${CsvHeaders.ALL}" }
    }

    fun validateRecord(record: CompanyRecord, region: String): ValidationResult {
        val violations = validator.validate(record)
        val errors = violations.map { "${it.propertyPath}: ${it.message}" }.toMutableList()

        if (!phoneValidator.isValid(record.phone, region)) {
            errors.add("phone: Invalid phone number")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}