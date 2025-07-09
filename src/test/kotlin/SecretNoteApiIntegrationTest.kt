package com.ivansuvorov.secretstash;

import com.fasterxml.jackson.module.kotlin.readValue
import com.ivansuvorov.secretstash.api.model.SecretNote
import com.ivansuvorov.secretstash.api.model.SecretNoteCreateRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant
import java.time.OffsetDateTime

class SecretNoteApiIntegrationTest : AbstractTest() {

    @Test
    fun `should be able to create a secret note`() {
        val expiresAt = Instant.now().plusSeconds(60L)

        val created = mockMvc.post("/notes") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = objectMapper.writeValueAsString(
                SecretNoteCreateRequest(
                    title = "Test",
                    content = "Test content",
                    expiresAt = expiresAt
                )
            )
        }
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<SecretNote>(it) }

        assertThat(created.title).isEqualTo("Test")
        assertThat(created.content).isEqualTo("Test content")

        val id = created.id

        val foundByid = mockMvc.get("/notes/$id") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isOk() } }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<SecretNote>(it) }

        assertThat(foundByid.id).isEqualTo(id)
        assertThat(foundByid.title).isEqualTo("Test")
        assertThat(foundByid.content).isEqualTo("Test content")
    }
}