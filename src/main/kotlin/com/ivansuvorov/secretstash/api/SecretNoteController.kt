package com.ivansuvorov.secretstash.api

import com.ivansuvorov.secretstash.api.model.SecretNote
import com.ivansuvorov.secretstash.api.model.SecretNoteCreateRequest
import com.ivansuvorov.secretstash.api.model.SecretNoteUpdateRequest
import com.ivansuvorov.secretstash.service.SecretNoteService
import com.ivansuvorov.secretstash.service.model.SecretNoteCreateRequestDto
import com.ivansuvorov.secretstash.service.model.SecretNoteDto
import com.ivansuvorov.secretstash.service.model.SecretNoteUpdateRequestDto
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/notes")
class SecretNoteController(
    private val secretNoteService: SecretNoteService
) {

    @PostMapping
    fun createSecretNote(@RequestBody createRequest: SecretNoteCreateRequest): SecretNote {
        val secretNote = secretNoteService.create(
            SecretNoteCreateRequestDto(
                title = createRequest.title,
                content = createRequest.content,
                expiresAt = createRequest.expiresAt
            )
        )
        return secretNote.toApiModel()
    }

    @GetMapping("/{id}")
    fun getSecretNote(@PathVariable id: UUID): SecretNote? {
        val secretNote = secretNoteService.findById(id)
        return secretNote?.toApiModel()
    }

    @PutMapping("/{id}")
    fun updateSecretNote(@PathVariable id: UUID, @RequestBody updateRequest: SecretNoteUpdateRequest): SecretNote {
        val secretNote = secretNoteService.update(
            id = id,
            request = SecretNoteUpdateRequestDto(
                title = updateRequest.title,
                content = updateRequest.content,
                expiresAt = updateRequest.expiresAt
            )
        )
        return secretNote.toApiModel()
    }

    @DeleteMapping("/{id}")
    fun deleteSecretNote(@PathVariable id: UUID) {
        secretNoteService.delete(id)
    }

    private fun SecretNoteDto.toApiModel(): SecretNote {
        return SecretNote(
            id = id,
            title = title,
            content = content,
            expiresAt = expiresAt,
            createdAt = createdAt
        )
    }
}