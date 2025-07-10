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

    /**
     * Creates a secure hash from the given user-supplied password. This hash can then be persisted.
     *
     * @param password Password from user request.
     * @return Password hash.
     */
    fun hashPassword(password: String): String = bCryptHasher.hashToString(COST, password.toCharArray())

    /**
     * Verifies the given password, comparing it to the given password hash.
     *
     * @param passwordToVerify Password to verify.
     * @param passwordHash Password hash.
     * @return true if the password is valid, false otherwise.
     */
    fun verifyPassword(
        passwordToVerify: String,
        passwordHash: String,
    ): Boolean = bCryptVerifyer
        .verify(
            passwordToVerify.toCharArray(),
            passwordHash.toCharArray(),
        ).verified
}
