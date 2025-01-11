package com.dbytes.uitilities

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JWTConfig {
    private const val secret = "your-secret-key"
    private const val issuer = "your-issuer"
    private const val audience = "your-audience"
    private const val validityInMs = 36_000_00 * 10 // 10 hours

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: String): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(algorithm)

    fun verifyToken() = JWT.require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}