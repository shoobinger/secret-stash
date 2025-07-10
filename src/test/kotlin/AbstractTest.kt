package com.ivansuvorov.secretstash

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ivansuvorov.secretstash.api.model.JwtTokenResponse
import com.ivansuvorov.secretstash.api.model.UserLoginRequest
import com.ivansuvorov.secretstash.api.model.UserRegistrationRequest
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.UUID

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

    protected fun registerUser(
        email: String = "test-${UUID.randomUUID()}@test.com",
        password: String = "123"
    ): String {
        mockMvc.post("/users/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                UserRegistrationRequest(
                    email = email,
                    password = password
                )
            )
        }.andExpect { status { isOk() } }

        val token = mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                UserLoginRequest(
                    email = email,
                    password = password
                )
            )
        }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<JwtTokenResponse>(it) }

        return token.token
    }
}