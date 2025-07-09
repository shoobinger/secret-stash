package com.ivansuvorov.secretstash.data.repository

import com.ivansuvorov.secretstash.data.model.SecretNoteDbModel
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SecretNoteRepository : CrudRepository<SecretNoteDbModel, UUID> {
}