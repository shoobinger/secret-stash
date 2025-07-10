package com.ivansuvorov.secretstash.service

import com.ivansuvorov.secretstash.api.model.JwtTokenResponse
import com.ivansuvorov.secretstash.api.model.UserLoginRequest
import com.ivansuvorov.secretstash.data.model.UserDbModel
import com.ivansuvorov.secretstash.data.repository.UserRepository
import com.ivansuvorov.secretstash.service.model.UserRegisterRequestDto
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userPasswordManager: UserPasswordManager,
    private val jwtManager: JwtManager,
    private val userRepository: UserRepository
) {

    @Transactional
    fun register(request: UserRegisterRequestDto) {
        if (userRepository.findByEmail(request.email) != null) {
            throw ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "User with this email already exists"
            )
        }

        val passwordHash = userPasswordManager.hashPassword(request.password)

        userRepository.save(
            UserDbModel(
                id = null,
                email = request.email,
                passwordHash = passwordHash
            )
        )
    }

    @Transactional
    fun login(request: UserLoginRequest): JwtTokenResponse {
        val user = userRepository.findByEmail(request.email)
        if (user == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid username or password")
        }

        val passwordValid = userPasswordManager.verifyPassword(
            passwordToVerify = request.password,
            passwordHash = user.passwordHash
        )

        if (!passwordValid) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password")
        }

        val token = jwtManager.buildToken(user.email)

        return JwtTokenResponse(
            token = token
        )
    }
}