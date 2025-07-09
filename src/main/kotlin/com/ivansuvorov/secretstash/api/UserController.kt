package com.ivansuvorov.secretstash.api

import com.ivansuvorov.secretstash.api.model.JwtTokenResponse
import com.ivansuvorov.secretstash.api.model.UserLoginRequest
import com.ivansuvorov.secretstash.api.model.UserRegistrationRequest
import com.ivansuvorov.secretstash.service.UserService
import com.ivansuvorov.secretstash.service.model.UserRegisterRequestDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/register")
    fun register(@RequestBody request: UserRegistrationRequest) {
        userService.register(
            UserRegisterRequestDto(
                email = request.email,
                password = request.password
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): JwtTokenResponse {
        return userService.login(
            UserLoginRequest(
                email = request.email,
                password = request.password
            )
        )
    }
}