package dev.lobanov.raynetassignment.integration.rcrm

import dev.lobanov.raynetassignment.config.RcrmProperties
import dev.lobanov.raynetassignment.integration.rcrm.dto.RcrmCompanySearchResponse
import dev.lobanov.raynetassignment.integration.rcrm.dto.RcrmCompanyUpsertRequest
import dev.lobanov.raynetassignment.integration.rcrm.dto.RcrmCompanyCreateResponse
import dev.lobanov.raynetassignment.integration.rcrm.dto.RcrmCompanyUpdateResponse
import dev.lobanov.raynetassignment.integration.rcrm.exception.RcrmRateLimitException
import dev.lobanov.raynetassignment.integration.rcrm.exception.RcrmUpsertException
import jakarta.annotation.PostConstruct
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.util.*

/**
 * HTTP client for interacting with the RCRM API.
 */
@Component
class RcrmHttpClient(
    private val restTemplate: RestTemplate,
    private val rateLimiter: RcrmRateLimiter,
    private val rcrmProps: RcrmProperties
) {
    companion object {
        const val INSTANCE_NAME_HEADER = "X-Instance-Name"
        const val RATELIMIT_LIMIT_HEADER = "X-Ratelimit-Limit"
        const val RATELIMIT_REMAINING_HEADER = "X-Ratelimit-Remaining"
        const val RATELIMIT_RESET_HEADER = "X-Ratelimit-Reset"
        const val RATELIMIT_REACHED_MESSAGE = "Rate limit reached"
    }

    @PostConstruct
    fun initialize() {
        val headers = createHeaders()
        val request = HttpEntity<Unit>(headers)

        try {
            val response = restTemplate.exchange(
                "${rcrmProps.baseUrl}/api/v2/company/",
                HttpMethod.GET,
                request,
                RcrmCompanySearchResponse::class.java
            )
            updateRateLimits(response.headers)
        } catch (ex: HttpClientErrorException.TooManyRequests) {
            ex.responseHeaders?.let { updateRateLimits(it) }
        }
    }

    fun findCompanyByRegNumber(regNumber: String): RcrmCompanySearchResponse? {
        if (!rateLimiter.tryReserveRequestLimit()) {
            throw RcrmRateLimitException(RATELIMIT_REACHED_MESSAGE)
        }

        val headers = createHeaders()
        val request = HttpEntity<Unit>(headers)

        val response = try {
            restTemplate.exchange(
                "${rcrmProps.baseUrl}/api/v2/company/?offset=0&limit=1&regNumber[LIKE]=$regNumber",
                HttpMethod.GET,
                request,
                RcrmCompanySearchResponse::class.java
            )
        } catch (ex: HttpClientErrorException.TooManyRequests) {
            ex.responseHeaders?.let { updateRateLimits(it) }
            throw RcrmRateLimitException(RATELIMIT_REACHED_MESSAGE, ex)
        }

        updateRateLimits(response.headers)
        handleResponse(response)
        return response.body ?: throw RcrmUpsertException("Empty response while looking for company")
    }

    fun createCompany(request: RcrmCompanyUpsertRequest): RcrmCompanyCreateResponse {
        if (!rateLimiter.tryReserveRequestLimit()) {
            throw RcrmRateLimitException(RATELIMIT_REACHED_MESSAGE)
        }

        val headers = createHeaders()
        val httpEntity = HttpEntity(request, headers)

        val response = try {
            restTemplate.exchange(
                "${rcrmProps.baseUrl}/api/v2/company/",
                HttpMethod.PUT,
                httpEntity,
                RcrmCompanyCreateResponse::class.java
            )
        } catch (ex: HttpClientErrorException.TooManyRequests) {
            ex.responseHeaders?.let { updateRateLimits(it) }
            throw RcrmRateLimitException(RATELIMIT_REACHED_MESSAGE, ex)
        }

        updateRateLimits(response.headers)
        handleResponse(response)
        return response.body ?: throw RcrmUpsertException("Empty response while creating company")
    }

    fun updateCompany(companyId: Long, request: RcrmCompanyUpsertRequest): RcrmCompanyUpdateResponse {
        if (!rateLimiter.tryReserveRequestLimit()) {
            throw RcrmRateLimitException(RATELIMIT_REACHED_MESSAGE)
        }

        val headers = createHeaders()
        val httpEntity = HttpEntity(request, headers)

        val response = try {
            restTemplate.exchange(
                "${rcrmProps.baseUrl}/api/v2/company/$companyId/",
                HttpMethod.POST,
                httpEntity,
                RcrmCompanyUpdateResponse::class.java
            )
        } catch (ex: HttpClientErrorException.TooManyRequests) {
            ex.responseHeaders?.let { updateRateLimits(it) }
            throw RcrmRateLimitException(RATELIMIT_REACHED_MESSAGE, ex)
        }

        updateRateLimits(response.headers)
        handleResponse(response)
        return response.body ?: throw RcrmUpsertException("Empty response while updating company")
    }

    private fun createHeaders() = HttpHeaders().apply {
        val credentials = "${rcrmProps.username}:${rcrmProps.apiKey}"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())
        set(HttpHeaders.AUTHORIZATION, "Basic $encodedCredentials")
        set(INSTANCE_NAME_HEADER, rcrmProps.instanceName)
        contentType = MediaType.APPLICATION_JSON
    }

    private fun updateRateLimits(headers: HttpHeaders) {
        val limit = headers[RATELIMIT_LIMIT_HEADER]?.firstOrNull()?.toInt()
        val remaining = headers[RATELIMIT_REMAINING_HEADER]?.firstOrNull()?.toInt()
        val reset = headers[RATELIMIT_RESET_HEADER]?.firstOrNull()?.toLong()

        if (limit != null && remaining != null && reset != null) {
            rateLimiter.updateLimits(limit, remaining, reset)
        }
    }

    private fun handleResponse(response: ResponseEntity<*>) {
        if (!response.statusCode.is2xxSuccessful) {
            throw RcrmUpsertException("Operation failed with status: ${response.statusCode}")
        }
    }
}
