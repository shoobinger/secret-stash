package com.ivansuvorov.secretstash

import com.fasterxml.jackson.module.kotlin.readValue
import com.ivansuvorov.secretstash.api.model.JwtTokenResponse
import com.ivansuvorov.secretstash.api.model.UserLoginRequest
import com.ivansuvorov.secretstash.api.model.UserRegistrationRequest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import java.util.UUID

class UserApiTest : AbstractTest() {
    @Test
    fun `should be able to register and login users`() {
        val email = "test-${UUID.randomUUID()}@test.com"
        mockMvc
            .post("/users/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        UserRegistrationRequest(
                            email = email,
                            password = "123456",
                        ),
                    )
            }.andExpect { status { isOk() } }

        mockMvc
            .post("/users/login") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        UserLoginRequest(
                            email = email,
                            password = "123456",
                        ),
                    )
            }.andReturn()
            .response.contentAsString
            .let { objectMapper.readValue<JwtTokenResponse>(it) }
    }

    @Test
    fun `should return 401 for invalid password`() {
        val email = "test-${UUID.randomUUID()}@test.com"
        mockMvc
            .post("/users/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        UserRegistrationRequest(
                            email = email,
                            password = "123456",
                        ),
                    )
            }.andExpect { status { isOk() } }

        mockMvc
            .post("/users/login") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        UserLoginRequest(
                            email = email,
                            password = "invalid",
                        ),
                    )
            }.andExpect { status { isUnauthorized() } }
            .andExpect { content { contentType(MediaType.APPLICATION_PROBLEM_JSON) } }
    }
}
