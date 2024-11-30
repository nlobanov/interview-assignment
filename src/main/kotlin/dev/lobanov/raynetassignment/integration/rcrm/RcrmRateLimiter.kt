package dev.lobanov.raynetassignment.integration.rcrm

import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

@Component
class RcrmRateLimiter {
    private var limit: Int? = null
    private var remaining: AtomicInteger = AtomicInteger(0)
    private var resetTime: Instant? = null
    private val lock = ReentrantLock()

    fun canMakeRequests(): Boolean {
        lock.lock()
        try {
            val now = Instant.now()
            if (resetTime != null && now.isAfter(resetTime)) {
                remaining.set(limit ?: 0)
                resetTime = null
            }

            return remaining.get() > 0
        } finally {
            lock.unlock()
        }
    }

    fun tryReserveRequestLimit(): Boolean {
        return remaining.updateAndGet { current ->
            if (current > 0) current - 1 else current
        } >= 0
    }

    fun updateLimits(limit: Int, remaining: Int, resetTime: Long) {
        lock.lock()
        try {
            this.limit = limit
            this.remaining.set(remaining)
            this.resetTime = Instant.ofEpochSecond(resetTime)
        } finally {
            lock.unlock()
        }
    }
}