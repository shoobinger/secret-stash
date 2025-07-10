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
import java.util.UUID

class SecretNoteApiTest : AbstractTest() {

    @Test
    fun `should be able to create a secret note`() {
        val userToken = registerUser()

        val created = mockMvc.post("/notes") {
            header("Authorization", "Bearer $userToken")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
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
            header("Authorization", "Bearer $userToken")
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
        val userToken = registerUser()
        val id = createNote(userToken)

        val updated = mockMvc.put("/notes/$id") {
            header("Authorization", "Bearer $userToken")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
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
        val userToken = registerUser()
        val id = createNote(userToken)

        mockMvc.delete("/notes/$id") {
            header("Authorization", "Bearer $userToken")
        }.andExpect {
            status { isOk() }
        }

        mockMvc.get("/notes/$id") {
            header("Authorization", "Bearer $userToken")
            accept = MediaType.APPLICATION_JSON
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun `should be able to retrieve latest notes`() {
        val userToken = registerUser()

        createNote(userToken)

        val count = 10
        // Create 10 more notes.
        val notes = (1..count).map {
            createNote(userToken)
        }

        // Retrieve latest notes (should not include the first note)
        val latestNotes = mockMvc.get("/notes?count=$count") {
            header("Authorization", "Bearer $userToken")
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<List<SecretNote>>(it) }

        assertThat(latestNotes).hasSize(count)
        assertThat(latestNotes.map { it.id }).isEqualTo(notes.reversed()) // sorted by creation date
    }

    @Test
    fun `should not be able to create a note without a JWT token`() {
        mockMvc.post("/notes") {
            // Not including any authorization header.
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                SecretNoteCreateRequest(
                    title = "Test",
                    content = "Test content",
                    expiresAt = Instant.now().plusSeconds(60L)
                )
            )
        }
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    fun `should not be able to access a note of a different user`() {
        val firstUserToken = registerUser()
        val secondUserToken = registerUser()

        // First user creates a new note.
        val id = createNote(firstUserToken)

        // Second user tries to access this note.
        mockMvc.get("/notes/$id") {
            header("Authorization", "Bearer $secondUserToken")
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isNotFound() } }

        mockMvc.put("/notes/$id") {
            header("Authorization", "Bearer $secondUserToken")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                SecretNoteUpdateRequest(
                    title = "Test (updated)",
                    content = "Test content (updated)",
                )
            )
        }
            .andExpect {
                status { isNotFound() }
            }

        mockMvc.delete("/notes/$id") {
            header("Authorization", "Bearer $secondUserToken")
        }.andExpect {
            status { isNotFound() }
        }
    }

    private fun createNote(userToken: String): UUID {
        val created = mockMvc.post("/notes") {
            header("Authorization", "Bearer $userToken")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                SecretNoteCreateRequest(
                    title = "Test",
                    content = "Test content",
                    expiresAt = Instant.now().plusSeconds(60L)
                )
            )
        }
            .andExpect { status { isOk() } }
            .andReturn()
            .response.contentAsString.let { objectMapper.readValue<SecretNote>(it) }

        return created.id
    }
}