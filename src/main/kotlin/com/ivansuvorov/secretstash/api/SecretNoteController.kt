package com.ivansuvorov.secretstash.api

import com.ivansuvorov.secretstash.api.model.SecretNote
import com.ivansuvorov.secretstash.api.model.SecretNoteCreateRequest
import com.ivansuvorov.secretstash.api.model.SecretNoteUpdateRequest
import com.ivansuvorov.secretstash.service.SecretNoteService
import com.ivansuvorov.secretstash.service.model.SecretNoteCreateRequestDto
import com.ivansuvorov.secretstash.service.model.SecretNoteDto
import com.ivansuvorov.secretstash.service.model.SecretNoteUpdateRequestDto
import com.ivansuvorov.secretstash.service.model.UserDto
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/notes")
class SecretNoteController(
    private val secretNoteService: SecretNoteService
) {

    @PostMapping
    fun createSecretNote(
        @RequestBody createRequest: SecretNoteCreateRequest,
        httpRequest: HttpServletRequest
    ): SecretNote {
        val secretNote = secretNoteService.create(
            caller = httpRequest.getAttribute("user") as UserDto,
            request = SecretNoteCreateRequestDto(
                title = createRequest.title,
                content = createRequest.content,
                expiresAt = createRequest.expiresAt
            )
        )
        return secretNote.toApiModel()
    }

    @GetMapping("/{id}")
    fun getSecretNote(
        @PathVariable id: UUID,
        httpRequest: HttpServletRequest
    ): SecretNote? {
        val secretNote = secretNoteService.findById(
            caller = httpRequest.getAttribute("user") as UserDto,
            noteId = id
        )
        return secretNote?.toApiModel()
    }

    @GetMapping
    fun getLatestSecretNotes(
        @RequestParam("count", required = false, defaultValue = "1000") count: Int,
        httpRequest: HttpServletRequest
    ): List<SecretNote> {
        if (count > 1000) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't retrieve more than 1000 notes")
        }

        val secretNotes = secretNoteService.findLatest(
            caller = httpRequest.getAttribute("user") as UserDto,
            count = count
        )
        return secretNotes.map { it.toApiModel() }
    }

    @PutMapping("/{id}")
    fun updateSecretNote(
        @PathVariable id: UUID,
        @RequestBody updateRequest: SecretNoteUpdateRequest,
        httpRequest: HttpServletRequest
    ): SecretNote {
        val secretNote = secretNoteService.update(
            caller = httpRequest.getAttribute("user") as UserDto,
            noteId = id,
            request = SecretNoteUpdateRequestDto(
                title = updateRequest.title,
                content = updateRequest.content,
                expiresAt = updateRequest.expiresAt
            )
        )
        return secretNote.toApiModel()
    }

    @DeleteMapping("/{id}")
    fun deleteSecretNote(@PathVariable id: UUID, httpRequest: HttpServletRequest) {
        secretNoteService.delete(
            caller = httpRequest.getAttribute("user") as UserDto,
            noteId = id
        )
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