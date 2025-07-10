package com.ivansuvorov.secretstash.service

import com.ivansuvorov.secretstash.configuration.properties.RateLimitProperties
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Service
class RateLimiterService(
    rateLimitProperties: RateLimitProperties
) {
    private val globalLimiter = RateLimiter.of(
        "global", RateLimiterConfig.custom()
            .limitForPeriod(rateLimitProperties.globalLimit)
            .limitRefreshPeriod(rateLimitProperties.period)
            .timeoutDuration(Duration.ofMillis(0))
            .build()
    )

    private val userConfig: RateLimiterConfig = RateLimiterConfig.custom()
        .limitForPeriod(rateLimitProperties.userLimit)
        .limitRefreshPeriod(rateLimitProperties.period)
        .timeoutDuration(Duration.ofMillis(0))
        .build()

    private val userLimiters: ConcurrentMap<String, RateLimiter> = ConcurrentHashMap()

    fun checkGlobal() {
        if (!globalLimiter.acquirePermission()) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS)
        }
    }

    fun checkForUser(userId: UUID) {
        val rateLimiter = userLimiters.computeIfAbsent(userId.toString()) { id ->
            RateLimiter.of("user-$id", userConfig)
        }

        if (!rateLimiter.acquirePermission()) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS)
        }
    }
}