package com.example.projetopokedex.data.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.util.Date

object JwtGenerator {
    private const val SECRET_KEY = "uma_secret_key_bem_grande_para_o_projeto_didatico_123456"

    private val key = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())

    fun generateToken(
        subject: String,
        expirationMillis: Long = 60 * 60 * 1000L // 1h
    ): String {
        val now = Date()
        val expiry = Date(now.time + expirationMillis)

        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}