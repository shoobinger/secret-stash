package com.ivansuvorov.secretstash.service

import com.ivansuvorov.secretstash.configuration.properties.JwtProperties
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.Ed25519Signer
import com.nimbusds.jose.crypto.Ed25519Verifier
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.OctetKeyPair
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import java.util.UUID


@Component
class JwtManager(
    private val properties: JwtProperties
) {
    var jwk: OctetKeyPair = OctetKeyPairGenerator(Curve.Ed25519)
        .keyID("123")
        .generate()

    val publicJWK: OctetKeyPair = jwk.toPublicJWK()
    val signer: JWSSigner = Ed25519Signer(jwk)
    val verifier: JWSVerifier = Ed25519Verifier(publicJWK)

    fun buildToken(userId: UUID): String {
        val claims = JWTClaimsSet.Builder()
            .subject(userId.toString())
            .issuer(properties.issuer)
            .expirationTime(Date.from(Instant.now().plusSeconds(properties.tokenExpiration.seconds)))
            .build()

        val jwt = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.EdDSA).keyID(jwk.keyID).build(),
            claims
        )
        jwt.sign(signer)
        return jwt.serialize()
    }

    fun verifyToken(token: String): String {
        val jwt = SignedJWT.parse(token)
        jwt.verify(verifier)

        val claims = jwt.jwtClaimsSet
        if (claims.issuer != properties.issuer) {
            TODO()
        }

        return jwt.jwtClaimsSet.subject
    }
}