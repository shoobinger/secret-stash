package com.ivansuvorov.secretstash

import com.fasterxml.jackson.module.kotlin.readValue
import com.ivansuvorov.secretstash.api.model.SecretNote
import com.ivansuvorov.secretstash.api.model.SecretNotePublicLink
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Duration

class SharedNotesApiTest : AbstractTest() {
    @Test
    fun `should be able to access public note`() {
        val creatorToken = registerUser()

        val noteId = createNote(creatorToken)

        val publicLink =
            mockMvc
                .post("/notes/$noteId/share") {
                    header("Authorization", "Bearer $creatorToken")
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }.andReturn()
                .response.contentAsString
                .let { objectMapper.readValue<SecretNotePublicLink>(it) }

        val note = mockMvc
            .get(publicLink.link) {
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andReturn()
            .response.contentAsString
            .let { objectMapper.readValue<SecretNote>(it) }

        assertThat(note.id).isEqualTo(noteId)
        assertThat(note.title).isEqualTo("Test")
    }

    @Test
    fun `should not be able to access public note after expiration`() {
        val creatorToken = registerUser()

        val noteId = createNote(creatorToken)

        val publicLink =
            mockMvc
                .post("/notes/$noteId/share") {
                    header("Authorization", "Bearer $creatorToken")
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }.andReturn()
                .response.contentAsString
                .let { objectMapper.readValue<SecretNotePublicLink>(it) }

        await atMost Duration.ofSeconds(15L) untilAsserted {
            mockMvc
                .get(publicLink.link) {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                }.andReturn()
                .response.contentAsString
                .let { objectMapper.readValue<SecretNote>(it) }
        }
    }
}
