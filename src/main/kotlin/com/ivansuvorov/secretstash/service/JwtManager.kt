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
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.Date
import java.util.UUID

/**
 * JwtManager is responsible for creating and verifying JWTs.
 * Only EdDSA tokens are supported without key rotation.
 * Key ID must be specified via properties.
 */
@Component
class JwtManager(
    private val properties: JwtProperties,
) {
    var jwk: OctetKeyPair =
        OctetKeyPairGenerator(Curve.Ed25519)
            .keyID(properties.keyId)
            .generate()

    val publicJWK: OctetKeyPair = jwk.toPublicJWK()
    val signer: JWSSigner = Ed25519Signer(jwk)
    val verifier: JWSVerifier = Ed25519Verifier(publicJWK)

    /**
     * Builds a base64-encoded JWT with a subject set to the given user ID.
     *
     * @param userId User ID.
     * @return Base64-encoded JWT.
     */
    fun buildToken(userId: UUID): String {
        val claims =
            JWTClaimsSet
                .Builder()
                .subject(userId.toString())
                .issuer(properties.issuer)
                .expirationTime(Date.from(Instant.now().plusSeconds(properties.tokenExpiration.seconds)))
                .build()

        val jwt = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.EdDSA).keyID(jwk.keyID).build(),
            claims,
        )
        jwt.sign(signer)
        return jwt.serialize()
    }

    /**
     * Verifies the given token. Throws exception if the given token is not valid.
     *
     * Implementation notes:
     * For a given token to be considered valid, it has to have a valid signature, it must have a valid `iss` claim
     * and not be expired.
     *
     * @param token Base64-encoded token to verify.
     * @return `sub` claim of a valid token.
     */
    fun verifyToken(token: String): String {
        val jwt = SignedJWT.parse(token)
        jwt.verify(verifier)

        val claims = jwt.jwtClaimsSet
        if (claims.issuer != properties.issuer) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid issuer")
        }

        if (claims.expirationTime.before(Date())) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is expired")
        }

        return jwt.jwtClaimsSet.subject
    }
}
