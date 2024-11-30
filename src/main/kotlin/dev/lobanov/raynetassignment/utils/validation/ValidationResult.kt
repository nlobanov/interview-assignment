package dev.lobanov.raynetassignment.utils.validation

/**
 * Result of record validation.
 * @property isValid true if record has no validation errors
 * @property errors list of validation error messages
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)
