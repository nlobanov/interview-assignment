package dev.lobanov.raynetassignment.integration.rcrm.exception

class RcrmRateLimitException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)