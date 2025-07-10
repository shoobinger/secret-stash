package com.ivansuvorov.secretstash.service

import at.favre.lib.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component

@Component
class UserPasswordManager {
    companion object {
        const val COST = 12
    }

    private val bCryptHasher = BCrypt.withDefaults()
    private val bCryptVerifyer = BCrypt.verifyer()

    fun hashPassword(password: String): String = bCryptHasher.hashToString(COST, password.toCharArray())

    fun verifyPassword(
        passwordToVerify: String,
        passwordHash: String,
    ): Boolean =
        bCryptVerifyer
            .verify(
                passwordToVerify.toCharArray(),
                passwordHash.toCharArray(),
            ).verified
}
