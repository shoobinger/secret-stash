package com.ivansuvorov.secretstash.api

import com.ivansuvorov.secretstash.api.model.SecretNote
import com.ivansuvorov.secretstash.service.RateLimiterService
import com.ivansuvorov.secretstash.service.SecretNoteService
import com.ivansuvorov.secretstash.service.model.SecretNoteDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/public")
class PublicController(
    private val secretNoteService: SecretNoteService,
    private val rateLimiterService: RateLimiterService,
) {

    @GetMapping("/note/{id}")
    fun getPublicNote(
        @PathVariable id: UUID
    ): SecretNote? {
//        rateLimiterService.checkForUser(user.id)

//        MDC.put("requestId", UUID.randomUUID().toString())
//        MDC.put("userId", user.id.toString())
//        MDC.put("secretNoteId", id.toString())

        val secretNote =
            secretNoteService.findPublicNote(id)
        return secretNote?.toApiModel()
    }

    private fun SecretNoteDto.toApiModel(): SecretNote = SecretNote(
        id = id,
        title = title,
        content = content,
        expiresAt = expiresAt,
        createdAt = createdAt,
    )
}
