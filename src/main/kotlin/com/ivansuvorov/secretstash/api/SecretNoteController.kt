package com.ivansuvorov.secretstash.api

import com.ivansuvorov.secretstash.api.model.SecretNote
import com.ivansuvorov.secretstash.api.model.SecretNoteCreateRequest
import com.ivansuvorov.secretstash.service.SecretNoteService
import com.ivansuvorov.secretstash.service.model.SecretNoteCreateRequestDto
import com.ivansuvorov.secretstash.service.model.SecretNoteDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    fun createSecretNote(@RequestBody apiModel: SecretNoteCreateRequest): SecretNote {
        val secretNote = secretNoteService.create(
            SecretNoteCreateRequestDto(
                title = apiModel.title,
                content = apiModel.content,
                expiresAt = apiModel.expiresAt
            )
        )
        return secretNote.toApiModel()
    }

    @GetMapping("/{id}")
    fun getSecretNote(@PathVariable id: UUID): SecretNote? {
        val secretNote = secretNoteService.findById(id)
        return secretNote?.toApiModel()
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