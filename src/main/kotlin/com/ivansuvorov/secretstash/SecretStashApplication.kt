package com.ivansuvorov.secretstash

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SecretStashApplication

fun main(args: Array<String>) {
    runApplication<SecretStashApplication>(*args)
}