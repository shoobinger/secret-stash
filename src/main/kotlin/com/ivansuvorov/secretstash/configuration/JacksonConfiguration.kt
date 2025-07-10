package com.ivansuvorov.secretstash.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.Instant
import java.time.format.DateTimeFormatterBuilder

@Configuration
class JacksonConfiguration {
    companion object {
        const val NUMBER_OF_FRACTIONAL_DIGITS = 3
    }

    @Bean
    fun customJacksonObjectMapperBuilder(): Jackson2ObjectMapperBuilderCustomizer =
        Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder
                .modules(
                    JavaTimeModule().apply {
                        val dateTimeFormatter =
                            DateTimeFormatterBuilder()
                                .appendInstant(NUMBER_OF_FRACTIONAL_DIGITS)
                                .toFormatter()

                        addSerializer(
                            Instant::class.java,
                            object : InstantSerializer(
                                INSTANCE,
                                false,
                                false,
                                dateTimeFormatter,
                            ) {},
                        )
                    },
                    KotlinModule.Builder().build(),
                    Jdk8Module(),
                ).timeZone("UTC")
        }

    @Bean
    @Primary
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper = builder.build()
}
