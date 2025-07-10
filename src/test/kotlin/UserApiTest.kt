package com.ivansuvorov.secretstash

import com.fasterxml.jackson.module.kotlin.readValue
import com.ivansuvorov.secretstash.api.model.JwtTokenResponse
import com.ivansuvorov.secretstash.api.model.UserLoginRequest
import com.ivansuvorov.secretstash.api.model.UserRegistrationRequest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class UserApiTest : AbstractTest() {

    @Test
    fun `should be able to register and login users`() {
        mockMvc.post("/users/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                UserRegistrationRequest(
                    email = "test@test.com",
                    password = "123456"
                )
            )
        }.andExpect { status { isOk() } }

        val jwtToken = mockMvc.post("/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                UserLoginRequest(
                    email = "test@test.com",
                    password = "123456"
                )
            )
        }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<JwtTokenResponse>(it) }

    }
}