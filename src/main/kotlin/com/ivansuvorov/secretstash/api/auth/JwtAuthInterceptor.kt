package com.ivansuvorov.secretstash.api.auth

import com.ivansuvorov.secretstash.service.JwtManager
import com.ivansuvorov.secretstash.service.SecretNoteService
import com.ivansuvorov.secretstash.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor
import java.util.UUID

/**
 * Spring MVP interceptor that handles JWT authentication.
 */
@Component
class JwtAuthInterceptor(
    private val userService: UserService,
    private val jwtManager: JwtManager,
) : HandlerInterceptor {
    companion object {
        const val USER_REQUEST_ATTRIBUTE = "user"
    }
    private val logger: Logger = LoggerFactory.getLogger(JwtAuthInterceptor::class.java)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header")
        }

        val token = authHeader.removePrefix("Bearer ")
        val userId = jwtManager.verifyToken(token)

        val user = userService.getUserById(UUID.fromString(userId))
        if (user == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user")
        }

        logger.debug("User {} authenticated", user.id)

        request.setAttribute(USER_REQUEST_ATTRIBUTE, user)

        return true
    }
}
