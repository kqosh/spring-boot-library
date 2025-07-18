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
        // Haal de "Authorization" header op qqqq
        val authHeader = request.getHeader("Authorization")

        // Controleer of het een Bearer token is
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7) // Verwijder "Bearer "
            
            // Maak een (nog) niet-geauthenticeerd token aan met de JWT-string als credential
            val authRequest = UsernamePasswordAuthenticationToken(null, token)
            
            try {
                // Laat de AuthenticationManager de juiste provider (onze JwtAuthenticationProvider) vinden
                val authentication = authenticationManager.authenticate(authRequest)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: Exception) {
                // Authenticatie mislukt, maak de security context leeg
                SecurityContextHolder.clearContext()
            }
        }
        
        filterChain.doFilter(request, response)
    }
}