package dev.lobanov.raynetassignment.service.upload

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CompanyRecord(
    @field:NotBlank(message = "Registration number is required")
    @field:Pattern(
        regexp = "^[0-9]{8}$",
        message = "Registration number must be exactly 8 digits"
    )
    val regNumber: String,

    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Phone is required")
    @field:Pattern(
        regexp = "^\\+?[0-9]{10,15}$",
        message = "Phone number must be between 10 and 15 digits, optionally starting with +"
    )
    val phone: String
)