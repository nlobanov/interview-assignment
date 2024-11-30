package dev.lobanov.raynetassignment.service.upload

import dev.lobanov.raynetassignment.model.Company
import dev.lobanov.raynetassignment.utils.CsvHeaders
import org.apache.commons.csv.CSVRecord

class CompanyMapper {
    fun toCompanyRecord(record: CSVRecord): CompanyRecord =
        CompanyRecord(
            title = record[CsvHeaders.TITLE].trim(),
            regNumber = record[CsvHeaders.REG_NUMBER].trim(),
            email = record[CsvHeaders.EMAIL].trim().lowercase(),
            phone = record[CsvHeaders.PHONE].trim()
        )

    fun toEntity(record: CompanyRecord): Company =
        Company(
            regNumber = record.regNumber,
            title = record.title,
            email = record.email,
            phone = record.phone
        )
}