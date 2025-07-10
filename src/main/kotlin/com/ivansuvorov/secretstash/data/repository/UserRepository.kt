package com.ivansuvorov.secretstash.data.repository

import com.ivansuvorov.secretstash.data.model.UserDbModel
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface UserRepository : CrudRepository<UserDbModel, UUID> {
    fun findByEmail(email: String): UserDbModel?
}
