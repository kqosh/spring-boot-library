package com.jdriven.library.presentation

import com.jdriven.library.service.UserService
import com.jdriven.library.service.model.CreateUserRequest
import com.jdriven.library.service.model.UserDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/users")
class UserController(private val service: UserService) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('USER')")
    fun findByName(@PathVariable(value = "username") username: String, authentication: Authentication): UserDto {
        logger.info("findByName ${username}")
        validateUser(username, authentication)
        return service.find(username) ?: throw NoResourceFoundException(HttpMethod.GET, "/users/${username}")
    }

    private fun validateUser(username: String, authentication: Authentication) {
        if (authentication.authorities.map { it -> it.authority }.contains("ROLE_ADMIN")) return // admin is allowed to do stuff for other users
        if (username != authentication.name) {
            logger.warn("username = $username != ${authentication.name} = authentication.name")
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "other user not allowed")
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun create(@RequestBody user: CreateUserRequest) {
        logger.info("create $user")
        service.create(user)
    }

    @PatchMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    fun enable(
        @PathVariable(value = "username") username: String,
        @RequestParam(required = false, defaultValue = "false") enabled: Boolean
    ) {
        logger.info("enable ${username} ${enabled}")
        service.enable(username, enabled) ?: throw NoResourceFoundException(HttpMethod.PATCH, "/users/${username}")
    }

    @PostMapping("/{username}/roles/{role}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun addRole(@PathVariable(value = "username") username: String, @PathVariable(value = "role") role: String) {
        logger.info("addRole $username $role")
        service.addRole(username, role)
    }

    @DeleteMapping("/{username}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRole(@PathVariable(value = "username") username: String, @PathVariable(value = "role") role: String) {
        logger.info("deleteRole $username $role")
        val wasDeleted = service.deleteRole(username, role)
        if (!wasDeleted) throw NoResourceFoundException(HttpMethod.DELETE, "/users/${username}/roles/${role}")
    }
}