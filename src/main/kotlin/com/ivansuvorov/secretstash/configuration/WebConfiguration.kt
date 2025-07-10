package com.ivansuvorov.secretstash.configuration

import com.ivansuvorov.secretstash.api.auth.JwtAuthInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration(
    private val jwtAuthInterceptor: JwtAuthInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(jwtAuthInterceptor)
            .addPathPatterns("/notes/**")
    }
}
