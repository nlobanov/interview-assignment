package dev.lobanov.raynetassignment.model

import jakarta.persistence.Embeddable
import jakarta.validation.constraints.Size

@Embeddable
data class SocialNetworkContact(
    @field:Size(max = 255)
    val facebook: String? = null,

    @field:Size(max = 255)
    val googleplus: String? = null,

    @field:Size(max = 255)
    val twitter: String? = null,

    @field:Size(max = 255)
    val linkedin: String? = null,

    @field:Size(max = 255)
    val pinterest: String? = null,

    @field:Size(max = 255)
    val instagram: String? = null,

    @field:Size(max = 255)
    val skype: String? = null,

    @field:Size(max = 255)
    val youtube: String? = null
)