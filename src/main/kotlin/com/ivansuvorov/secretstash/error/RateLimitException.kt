package com.ivansuvorov.secretstash.error

class RateLimitException : RuntimeException("Reached rate limit, please wait and then try again")