package com.ivansuvorov.secretstash.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("ratelimit")
data class RateLimitProperties(
    val period: Duration,
    val userLimit: Int,
    val globalLimit: Int
)
