package dev.lobanov.raynetassignment.service.upsert

import dev.lobanov.raynetassignment.model.Company
import dev.lobanov.raynetassignment.model.UpsertStatus
import dev.lobanov.raynetassignment.repository.CompanyRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CompanyUpsertStatusManager(
    private val repository: CompanyRepository
) {
    @Transactional
    fun markAsCompleted(company: Company) {
        company.apply {
            upsertStatus = UpsertStatus.UPSERTED
            uploadError = null
        }
        repository.save(company)
    }

    @Transactional
    fun markAsFailed(company: Company, errorMessage: String) {
        company.apply {
            upsertStatus = UpsertStatus.FAILED
            uploadError = errorMessage
        }
        repository.save(company)
    }
}