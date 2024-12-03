package dev.lobanov.raynetassignment.service.upsert

import dev.lobanov.raynetassignment.config.UpsertProperties
import dev.lobanov.raynetassignment.integration.rcrm.RcrmHttpClient
import dev.lobanov.raynetassignment.integration.rcrm.RcrmRateLimiter
import dev.lobanov.raynetassignment.integration.rcrm.RcrmRequestMapper
import dev.lobanov.raynetassignment.integration.rcrm.exception.RcrmRateLimitException
import dev.lobanov.raynetassignment.integration.rcrm.exception.RcrmUpsertException
import dev.lobanov.raynetassignment.model.Company
import dev.lobanov.raynetassignment.model.UpsertStatus
import dev.lobanov.raynetassignment.repository.CompanyRepository
import dev.lobanov.raynetassignment.service.notification.CompanyEmailService
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

private val logger = KotlinLogging.logger {}

@Service
class CompanyUpsertService(
    private val repository: CompanyRepository,
    private val rcrmClient: RcrmHttpClient,
    private val rcrmRateLimiter: RcrmRateLimiter,
    private val statusManager: CompanyUpsertStatusManager,
    private val upsertProps: UpsertProperties,
    private val companyEmailService: CompanyEmailService
) {
    private val mapper = RcrmRequestMapper()
    private val activeUpserts = AtomicInteger(0)

    @Scheduled(
        fixedDelayString = "\${features.upsert.fixed-delay}",
        initialDelayString = "\${features.upsert.initial-delay}"
    )
    fun initiateUpserting() {
        logger.info { "Starting upsert..." }

        if (!canStartUpsert()) {
            return
        }

        runCatching {
            val stats = executeUpsert()
            notifyIfNeeded(stats)
            logger.info { "Completed upserting ${stats.totalProcessed} companies" }
        }.onFailure { e ->
            logger.error(e) { "Failed to process uploads" }
        }.also {
            activeUpserts.set(0)
        }
    }

    // Processes companies in batches with sequence, uses lastRegNumber to track where we finished in last batch
    // Finishes when the last batch returned an empty string instead of lastRegNumber
    private fun executeUpsert(): UpsertStatistics {
        val stats = UpsertStatistics()
        var lastRegNumber = ""

        generateSequence {
            getNextBatchAndUpsert(lastRegNumber, stats).also {
                lastRegNumber = it
            }
        }
            .takeWhile { it.isNotEmpty() }
            .lastOrNull()

        return stats
    }

    // Fetches a batch of companies, performs upsert in chunks, returns lastRegNumber to know where we finished
    private fun getNextBatchAndUpsert(lastRegNumber: String, stats: UpsertStatistics): String {
        // Look for PENDING companies starting from where we finished last batch
        val companies = repository.findByUpsertStatusAndRegNumberGreaterThanOrderByRegNumber(
            upsertStatus = UpsertStatus.PENDING,
            regNumber = lastRegNumber,
            pageable = PageRequest.of(0, upsertProps.pageSize)
        )

        // No companies left
        if (companies.isEmpty) {
            return ""
        }

        companies
            .chunked(upsertProps.chunkSize)
            .takeWhile { rcrmRateLimiter.canMakeRequests() }
            .forEach { chunk ->
                upsertCompanies(chunk)
                stats.updateStats(chunk)
            }

        return companies.last().regNumber
    }

    private fun upsertCompanies(companies: List<Company?>) {
        activeUpserts.addAndGet(companies.size)

        // Asynchronously upsert all companies in the list and wait until finish
        try {
            val futures = companies.map { company ->
                CompletableFuture
                    .supplyAsync { company?.let { upsertCompany(company) } }
                    .exceptionally { throwable ->
                        company?.apply {
                            throwable.message?.let { statusManager.markAsFailed(this, it) }
                        }
                    }
            }

            CompletableFuture.allOf(*futures.toTypedArray()).join()
        } finally {
            activeUpserts.addAndGet(-companies.size)
        }
    }

    // Looks for companies in RCRM by companyToUpsert regNumber, creates new or updates existing companies
    // and changes the status of companyToUpsert.
    private fun upsertCompany(companyToUpsert: Company): Company? {
        try {
            // If rate limit is reached, skip this company until next scheduled run
            val existingRcrmCompanies = try {
                rcrmClient.findCompanyByRegNumber(companyToUpsert.regNumber)
            } catch (e: RcrmRateLimitException) {
                logger.debug { "Rate limit reached, skipping company ${companyToUpsert.regNumber} until next scheduled run" }
                return null
            }

            if ((existingRcrmCompanies?.totalCount ?: 0) >= upsertProps.maxCompanyLimit) {
                throw RcrmUpsertException("RCRM system has reached maximum company limit")
            }

            val request = mapper.toRequest(companyToUpsert)

            try {
                if (!existingRcrmCompanies?.data.isNullOrEmpty()) {
                    existingRcrmCompanies?.data?.forEach { rcrmCompany ->
                        rcrmClient.updateCompany(rcrmCompany.id, request)
                    }
                } else {
                    rcrmClient.createCompany(request)
                }
            } catch (e: RcrmRateLimitException) {
                logger.debug { "Rate limit reached, skipping company ${companyToUpsert.regNumber} until next scheduled run" }
                return null
            }

            statusManager.markAsCompleted(companyToUpsert)
            logger.debug { "Successfully upserted company with regNumber: ${companyToUpsert.regNumber}" }
        } catch (e: Exception) {
            e.message?.let { statusManager.markAsFailed(companyToUpsert, it) }
            logger.error(e) { "Failed to upsert company with regNumber: ${companyToUpsert.regNumber}" }
        }

        return companyToUpsert
    }

    private fun canStartUpsert(): Boolean {
        if (activeUpserts.get() > 0) {
            logger.info { "Previous upsert still running, skipping..." }
            return false
        }

        if (!rcrmRateLimiter.canMakeRequests()) {
            logger.info { "Rate limit reached for RCRM instance, skipping upsert..." }
            return false
        }

        return true
    }

    private fun notifyIfNeeded(stats: UpsertStatistics) {
        if (stats.totalProcessed > 0) {
            companyEmailService.sendUpsertReport(stats)
        }
    }
}