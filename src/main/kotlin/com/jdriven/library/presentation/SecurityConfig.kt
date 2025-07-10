package com.jdriven.library.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
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
    fun userDetailsService(): InMemoryUserDetailsManager {//qqqq JdbcUserDetailsManager
        val admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("pwadmin")
            .roles("ADMIN", "USER")
            .build()
        val memeber1 = User.withDefaultPasswordEncoder()
            .username("nr101")
            .password("pwuser")
            .roles("USER")
            .build()
        val memeber2 = User.withDefaultPasswordEncoder()
            .username("nr102")
            .password("pwuser")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(admin, memeber1, memeber2)
    }
//
//    @Bean
//    fun roleHierarchy(): RoleHierarchy = RoleHierarchyImpl.fromHierarchy("ADMIN > USER")qqqq

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
