package com.ivansuvorov.secretstash.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("public")
data class PublicProperties(
    val host: String
)
