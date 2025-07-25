package com.jdriven.library.presentation

import com.jdriven.library.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/roles")
class RoleController(private val service: UserService) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "Find all available roles.")
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiStandardErrors
    fun findAll(): List<String> {
        logger.info("findAll")
        return service.findAllRoles() ?: throw NoResourceFoundException(HttpMethod.GET, "/roles")
    }
}