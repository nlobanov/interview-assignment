package dev.lobanov.raynetassignment.integration.rcrm.dto

data class RcrmCompanyUpsertRequest(
    val name: String,
    val rating: String = "A",
    val state: String = "A_POTENTIAL",
    val role: String = "A_SUBSCRIBER",
    val regNumber: String,
    val taxPayer: String = "YES",
    val addresses: List<AddressWrapper>
) {
    data class AddressWrapper(
        val address: Address,
        val contactInfo: ContactInfo
    ) {
        data class Address(
            val name: String = "Main address"
        )

        data class ContactInfo(
            val email: String,
            val tel1: String
        )
    }
}