package com.ivansuvorov.secretstash

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.time.Duration
import java.time.temporal.ChronoUnit

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
abstract class AbstractTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    companion object {
        @JvmStatic
        private val postgres =
            GenericContainer("postgres:16")
                .withExposedPorts(5432)
                .waitingFor(
                    LogMessageWaitStrategy()
                        .withRegEx(".*database system is ready to accept connections.*\\s")
                        .withTimes(2)
                        .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)),
                )
                .withCommand("postgres", "-c", "fsync=off")
                .withEnv(
                    mapOf(
                        "POSTGRES_DB" to "test",
                        "POSTGRES_USER" to "test",
                        "POSTGRES_PASSWORD" to "test",
                    ),
                )
                .withNetworkAliases("postgres")
                .apply { start() }


        @JvmStatic
        @DynamicPropertySource
        fun setup(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") {
                "jdbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/test"
            }
            registry.add("spring.datasource.username") { "test" }
            registry.add("spring.datasource.password") { "test" }
        }
    }
}