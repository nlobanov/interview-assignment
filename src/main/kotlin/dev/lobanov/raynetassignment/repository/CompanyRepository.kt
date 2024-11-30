package dev.lobanov.raynetassignment.repository

import dev.lobanov.raynetassignment.model.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: JpaRepository<Company, Long> {

}
