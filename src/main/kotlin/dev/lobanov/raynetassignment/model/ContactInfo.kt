package dev.lobanov.raynetassignment.model

import jakarta.persistence.Embeddable
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

@Embeddable
data class ContactInfo(
    @field:Email
    val email: String? = null,

    @field:Email
    val email2: String? = null,

    val fax: String? = null,
    val otherContact: String? = null,
    val tel1Type: String? = null,
    val tel1: String? = null,
    val tel2Type: String? = null,
    val tel2: String? = null,

    @field:Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-zA-Z]{2,}.*$")
    val www: String? = null,

    val doNotSendMM: Boolean = false
)
