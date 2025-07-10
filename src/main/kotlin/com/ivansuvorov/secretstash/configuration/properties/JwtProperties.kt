package com.ivansuvorov.secretstash.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("jwt")
data class JwtProperties(
    val keyId: String,
    val issuer: String,
    val tokenExpiration: Duration,
)
