package dev.lobanov.raynetassignment.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "company_addresses")
data class CompanyAddress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Embedded
    val address: Address,

    @Embedded
    val contactInfo: ContactInfo,

    @field:NotNull
    val territory: Long
)
