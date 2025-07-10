package com.ivansuvorov.secretstash.api.auth

import com.ivansuvorov.secretstash.service.JwtManager
import com.ivansuvorov.secretstash.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.UUID

@Component
class JwtAuthInterceptor(
    private val userService: UserService,
    private val jwtManager: JwtManager
) : HandlerInterceptor {

    @Throws(Exception::class)
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Missing or invalid Authorization header.")
            return false
        }

        val token = authHeader.removePrefix("Bearer ")
        val userId = jwtManager.verifyToken(token) // TODO handle error

        val user = userService.getUserById(UUID.fromString(userId))
        if (user == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("User not found.")
            return false
        }

        request.setAttribute("user", user)

        return true
    }
}
