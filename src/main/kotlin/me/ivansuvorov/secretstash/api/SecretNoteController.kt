package me.ivansuvorov.secretstash.api

import me.ivansuvorov.secretstash.api.model.SecretNoteApiModel
import me.ivansuvorov.secretstash.api.model.SecretNoteCreateApiModel
import me.ivansuvorov.secretstash.service.SecretNoteService
import me.ivansuvorov.secretstash.service.model.SecretNote
import me.ivansuvorov.secretstash.service.model.SecretNoteCreateRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/notes")
class SecretNoteController(
    private val secretNoteService: SecretNoteService
) {

    @PostMapping
    fun createSecretNote(apiModel: SecretNoteCreateApiModel): SecretNoteApiModel {
        val secretNote = secretNoteService.create(
            SecretNoteCreateRequest(
                title = apiModel.title,
                content = apiModel.content,
                expiresAt = apiModel.expiresAt
            )
        )
        return secretNote.toApiModel()
    }

    @GetMapping("/{id}")
    fun getSecretNote(@RequestParam id: UUID): SecretNoteApiModel? {
        val secretNote = secretNoteService.findById(id)
        return secretNote?.toApiModel()
    }

    private fun SecretNote.toApiModel(): SecretNoteApiModel {
        return SecretNoteApiModel(
            id = id,
            title = title,
            content = content,
            expiresAt = expiresAt
        )
    }
}