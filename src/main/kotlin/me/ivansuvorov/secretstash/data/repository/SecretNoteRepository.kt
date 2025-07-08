package me.ivansuvorov.secretstash.data.repository

import me.ivansuvorov.secretstash.data.model.SecretNoteDbModel
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SecretNoteRepository : CrudRepository<SecretNoteDbModel, UUID> {
}