package dev.lobanov.raynetassignment.repository

import dev.lobanov.raynetassignment.model.Company
import dev.lobanov.raynetassignment.model.UpsertStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
    fun findByUpsertStatusAndRegNumberGreaterThanOrderByRegNumber(
        upsertStatus: UpsertStatus,
        regNumber: String,
        pageable: Pageable
    ): Page<Company>
}