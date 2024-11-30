package dev.lobanov.raynetassignment.model

import jakarta.persistence.Embeddable
import jakarta.validation.constraints.*
import java.math.BigDecimal

@Embeddable
data class Address(
    @field:NotBlank
    @field:Size(max = 255)
    val name: String,

    @field:Size(max = 255)
    val street: String? = null,

    @field:Size(max = 255)
    val city: String? = null,

    @field:Size(max = 255)
    val province: String? = null,

    @field:Pattern(regexp = "\\d{3}\\s?\\d{2}")
    val zipCode: String? = null,

    @field:Size(min = 2, max = 2)
    val country: String? = null,

    @field:DecimalMin("-90.0")
    @field:DecimalMax("90.0")
    val lat: BigDecimal? = null,

    @field:DecimalMin("-180.0")
    @field:DecimalMax("180.0")
    val lng: BigDecimal? = null
)