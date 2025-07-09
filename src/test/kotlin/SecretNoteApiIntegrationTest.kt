package com.ivansuvorov.secretstash;

import com.fasterxml.jackson.module.kotlin.readValue
import com.ivansuvorov.secretstash.api.model.SecretNote
import com.ivansuvorov.secretstash.api.model.SecretNoteCreateRequest
import com.ivansuvorov.secretstash.api.model.SecretNoteUpdateRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.Instant

class SecretNoteApiIntegrationTest : AbstractTest() {

    @Test
    fun `should be able to create a secret note`() {
        val created = mockMvc.post("/notes") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = objectMapper.writeValueAsString(
                SecretNoteCreateRequest(
                    title = "Test",
                    content = "Test content",
                    expiresAt = Instant.now().plusSeconds(60L)
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

    @Test
    fun `should be able to update a secret note`() {
        val created = mockMvc.post("/notes") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = objectMapper.writeValueAsString(
                SecretNoteCreateRequest(
                    title = "Test",
                    content = "Test content",
                    expiresAt = Instant.now().plusSeconds(60L)
                )
            )
        }
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<SecretNote>(it) }

        val id = created.id

        val updated = mockMvc.put("/notes/$id") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = objectMapper.writeValueAsString(
                SecretNoteUpdateRequest(
                    title = "Test (updated)",
                    content = "Test content (updated)",
                    expiresAt = Instant.now().plusSeconds(120L)
                )
            )
        }
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<SecretNote>(it) }

        assertThat(updated.id).isEqualTo(id)
        assertThat(updated.title).isEqualTo("Test (updated)")
        assertThat(updated.content).isEqualTo("Test content (updated)")
    }

    @Test
    fun `should be able to delete a secret note`() {
        val created = mockMvc.post("/notes") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = objectMapper.writeValueAsString(
                SecretNoteCreateRequest(
                    title = "Test",
                    content = "Test content",
                    expiresAt = Instant.now().plusSeconds(60L)
                )
            )
        }
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<SecretNote>(it) }

        val id = created.id

        mockMvc.delete("/notes/$id")
            .andExpect {
                status { isOk() }
            }

        mockMvc.get("/notes/$id") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isNotFound() } }
    }
}