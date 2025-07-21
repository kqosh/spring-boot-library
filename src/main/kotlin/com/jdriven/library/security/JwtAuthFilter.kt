package com.jdriven.library.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthFilter(private val authenticationManager: AuthenticationManager) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7) // Remove "Bearer "
            
            // Create a not yet authenticated token.
            val authRequest = UsernamePasswordAuthenticationToken(null, token)
            
            try {
                // Let the AuthenticationManager find the correct provider (our JwtAuthenticationProvider).
                val authentication = authenticationManager.authenticate(authRequest)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: Exception) {
                // Authentication failed, clear security context.
                SecurityContextHolder.clearContext()
            }
        }
        
        filterChain.doFilter(request, response)
    }
}