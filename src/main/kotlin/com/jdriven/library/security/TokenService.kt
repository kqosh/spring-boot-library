package com.jdriven.library.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class TokenService(@Value("\${jwt.secret}") val jwtSecret: String) {//qqqq private val

    private val key: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun createToken(username: String, roles: List<String>): String {
        val now = Date()
        val expirationDate = Date(now.time + 3_600_000) // valid for 1 hour

        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expirationDate)
            .signWith(key)
            .compact()
    }

    fun getRoles(claims: Claims?): List<String> {
        return if (claims == null) emptyList()
        else claims.get("roles", List::class.java) as List<String>
    }

    fun parseToken(token: String): Claims? =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}