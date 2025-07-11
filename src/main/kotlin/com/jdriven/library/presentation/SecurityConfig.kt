package com.jdriven.library.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import javax.sql.DataSource

@Configuration
@EnableMethodSecurity // Enables @PreAuthorize
class SecurityConfig {

// Following bean can be useful for testing. NB '.roles(...)' automatically adds a "ROLE_" prefix.
//    @Bean
//    fun userDetailsService(): InMemoryUserDetailsManager {//qqqq JdbcUserDetailsManager
//        val admin = User.withDefaultPasswordEncoder()
//            .username("admin")
//            .password("pwadmin")
//            .roles("ADMIN", "USER")
//            .build()
//        val memeber1 = User.withDefaultPasswordEncoder()
//            .username("nr101")
//            .password("pwuser")
//            .roles("USER")
//            .build()
//        val memeber2 = User.withDefaultPasswordEncoder()
//            .username("nr102")
//            .password("pwuser")
//            .roles("USER")
//            .build()
//        return InMemoryUserDetailsManager(admin, memeber1, memeber2)
//    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Bean
    fun userDetailsManager(dataSource: DataSource, passwordEncoder: PasswordEncoder): UserDetailsManager = JdbcUserDetailsManager(dataSource)

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
