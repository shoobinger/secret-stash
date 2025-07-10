package com.ivansuvorov.secretstash.service

import com.ivansuvorov.secretstash.api.model.JwtTokenResponse
import com.ivansuvorov.secretstash.api.model.UserLoginRequest
import com.ivansuvorov.secretstash.data.model.UserDbModel
import com.ivansuvorov.secretstash.data.repository.UserRepository
import com.ivansuvorov.secretstash.service.model.UserDto
import com.ivansuvorov.secretstash.service.model.UserRegisterRequestDto
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserService(
    private val userPasswordManager: UserPasswordManager,
    private val jwtManager: JwtManager,
    private val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun register(request: UserRegisterRequestDto) {
        if (userRepository.findByEmail(request.email) != null) {
            throw ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "User with this email already exists",
            )
        }
        logger.info("Registering a new user")
        val passwordHash = userPasswordManager.hashPassword(request.password)
        val user = userRepository.save(
            UserDbModel(
                id = null,
                email = request.email,
                passwordHash = passwordHash,
            ),
        )
        logger.info("A new user with ID ${user.id} was successfully registered")
    }

    @Transactional
    fun login(request: UserLoginRequest): JwtTokenResponse {
        val user = userRepository.findByEmail(request.email)
        if (user == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password")
        }

        val passwordValid =
            userPasswordManager.verifyPassword(
                passwordToVerify = request.password,
                passwordHash = user.passwordHash,
            )
        if (!passwordValid) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password")
        }
        val token = jwtManager.buildToken(checkNotNull(user.id))
        return JwtTokenResponse(
            token = token,
        )
    }

    fun getUserById(userId: UUID): UserDto? = userRepository.findByIdOrNull(userId)?.toDto()

    private fun UserDbModel.toDto(): UserDto =
        UserDto(
            id = checkNotNull(id),
            email = email,
        )
}
