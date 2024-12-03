package dev.lobanov.raynetassignment.service.upsert

import dev.lobanov.raynetassignment.model.Company
import dev.lobanov.raynetassignment.model.UpsertStatus

/**
 * Tracks statistics for company upsert operations.
 *
 * @property totalProcessed Total number of companies processed through upsert operations
 * @property successful Number of companies successfully upserted
 * @property failed Number of companies that failed to upsert
 */
class UpsertStatistics {
    var totalProcessed: Int = 0
        private set
    var successful: Int = 0
        private set
    var failed: Int = 0
        private set

    fun updateStats(companies: List<Company>) {
        totalProcessed += companies.size
        successful += companies.count { it.upsertStatus == UpsertStatus.UPSERTED }
        failed += companies.count { it.upsertStatus == UpsertStatus.FAILED }
    }
}