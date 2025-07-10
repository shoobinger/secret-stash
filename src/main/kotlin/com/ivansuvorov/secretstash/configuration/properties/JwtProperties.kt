package com.ivansuvorov.secretstash.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("jwt")
data class JwtProperties(
    val issuer: String,
    val tokenExpiration: Duration
)
