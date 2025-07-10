package com.ivansuvorov.secretstash.api

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SwaggerUiController {
    @GetMapping("/swagger-ui")
    fun swaggerUi(): String = "redirect:/webjars/swagger-ui/index.html?url=/openapi.yaml"
}
