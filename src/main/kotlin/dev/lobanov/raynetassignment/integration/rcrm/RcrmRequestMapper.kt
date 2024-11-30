package dev.lobanov.raynetassignment.integration.rcrm

import dev.lobanov.raynetassignment.integration.rcrm.dto.RcrmCompanyUpsertRequest
import dev.lobanov.raynetassignment.model.Company

class RcrmRequestMapper {
    fun toRequest(company: Company): RcrmCompanyUpsertRequest {
        val contactInfo = RcrmCompanyUpsertRequest.AddressWrapper.ContactInfo(
            email = company.email,
            tel1 = company.phone
        )
        val address = RcrmCompanyUpsertRequest.AddressWrapper.Address()
        val addressWrapper = RcrmCompanyUpsertRequest.AddressWrapper(
            address = address,
            contactInfo = contactInfo
        )

        return RcrmCompanyUpsertRequest(
            name = company.title,
            regNumber = company.regNumber,
            addresses = listOf(addressWrapper)
        )
    }
}