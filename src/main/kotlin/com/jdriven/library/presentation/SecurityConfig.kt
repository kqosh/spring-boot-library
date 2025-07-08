package com.jdriven.library.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity // Enables @PreAuthorize
class SecurityConfig {

    // Definieer een admin- en een user-gebruiker voor de test qqqq eng
    @Bean
    fun userDetailsService(): InMemoryUserDetailsManager {
        val admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("pwadmin")
//            .roles("ADMIN", "USER")qqqq
            .roles("ADMIN")
            .build()
        val user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("pwuser")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(admin, user)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .httpBasic { }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/version").permitAll()
                    .anyRequest().authenticated()
            }
        return http.build()
    }
}
