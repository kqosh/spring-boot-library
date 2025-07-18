package com.jdriven.library.security

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(private val tokenService: TokenService) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication.credentials as? String
            ?: throw BadCredentialsException("Token not found in request")

        val claims = tokenService.parseToken(token)
            ?: throw BadCredentialsException("Invalid or expired JWT")

        val username = claims.subject
        val roles = tokenService.getRoles(claims).map { SimpleGrantedAuthority(it) }

        if (roles.isEmpty()) throw AccessDeniedException("Insufficient permissions")

        return UsernamePasswordAuthenticationToken(username, null, roles)
    }

    // Ensure this provider is used by our service only
    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}