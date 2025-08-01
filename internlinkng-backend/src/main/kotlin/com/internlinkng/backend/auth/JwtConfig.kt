package com.internlinkng.backend.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {
    private const val SECRET = "your-secret-key-here"
    private const val ISSUER = "internlinkng"
    private const val AUDIENCE = "internlinkng-users"
    private const val EXPIRES_IN = 3600L * 24 * 7 // 7 days

    fun makeToken(userId: String, isAdmin: Boolean): String {
        return JWT.create()
            .withSubject(userId)
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("isAdmin", isAdmin)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRES_IN * 1000))
            .sign(Algorithm.HMAC256(SECRET))
    }

    fun verifyToken(token: String): String? {
        return try {
            val verifier = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .build()
            verifier.verify(token).subject
        } catch (e: Exception) {
            null
        }
    }
} 