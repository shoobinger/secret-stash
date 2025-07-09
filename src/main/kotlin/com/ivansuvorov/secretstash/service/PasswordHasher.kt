package com.ivansuvorov.secretstash.service

import at.favre.lib.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component

@Component
class PasswordHasher {
    companion object {
        const val COST = 12
    }

    fun hashPassword(password: CharArray): CharArray {
        return BCrypt.withDefaults().hashToChar(COST, password)
    }

    fun verifyPassword(passwordToVerify: CharArray, passwordHash: CharArray): Boolean {
        return BCrypt.verifyer().verify(passwordToVerify, passwordHash).verified
    }
}