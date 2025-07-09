package com.ivansuvorov.secretstash.service

import org.springframework.stereotype.Component

@Component
class JwtManager {
    fun buildToken(email: String): String {
        TODO()
    }

    fun verifyToken(token: String): Boolean {
        TODO()
    }

}