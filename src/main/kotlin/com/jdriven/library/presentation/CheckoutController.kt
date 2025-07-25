package com.jdriven.library.presentation

import com.jdriven.library.service.CheckoutService
import com.jdriven.library.service.model.CheckoutDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/checkouts")
class CheckoutController(private val service: CheckoutService) {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@Operation(summary = "Find all checkouts for given username.")
	@GetMapping("/{username}")
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun findByUsername(@PathVariable(value = "username") username: String, authentication: Authentication): List<CheckoutDto> {
		logger.info("findByUsername $username")
		validateUser(username, authentication)
		return service.findByUsername(username) ?: throw NoResourceFoundException(HttpMethod.GET, "/checkouts/${username}")
	}

	private fun validateUser(username: String, authentication: Authentication) {
		if (authentication.authorities.map { it -> it.authority }.contains("ROLE_ADMIN")) return // admin is allowed to do stuff for other users
		if (username != authentication.name) {
			logger.warn("username = $username != ${authentication.name} = authentication.name")
			throw ResponseStatusException(HttpStatus.FORBIDDEN, "other user not allowed")
		}
	}

	@Operation(summary = "Find all checkouts for given ISBN.")
	@GetMapping("/book/{isbn}")
	@PreAuthorize("hasRole('ADMIN')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun findByBook(@PathVariable(value = "isbn") isbn: String): List<CheckoutDto> {
		logger.info("findByBook $isbn")
		return service.findByIsbn(isbn) ?: throw NoResourceFoundException(HttpMethod.GET, "/checkouts/book/${isbn}")
	}

	@Operation(summary = "Checkout a book for given username and ISBN.")
	@PostMapping("{username}/{isbn}")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "201", description = "Created")
	@ApiStandardErrors
	fun create(@PathVariable(value = "username") username: String, @PathVariable(value = "isbn") isbn: String, authentication: Authentication) {
		logger.info("create $username, $isbn")
		validateUser(username, authentication)
		service.create(username, isbn) ?: throw NoResourceFoundException(HttpMethod.POST, "/checkouts/${username}/${isbn}")
	}

	@Operation(summary = "Return a book for given username and ISBN.")
	@PatchMapping("{username}/{isbn}/return")
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun returnBook(@PathVariable(value = "username") username: String, @PathVariable(value = "isbn") isbn: String, authentication: Authentication) {
		logger.info("returnBook $username, $isbn")
		validateUser(username, authentication)
		service.returnBook(username, isbn) ?: throw NoResourceFoundException(HttpMethod.PATCH, "/checkouts/${username}/${isbn}/return")
	}

	@Operation(summary = "Renew a book for given username and ISBN.")
	@PatchMapping("{username}/{isbn}/renew")
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun renewBook(@PathVariable(value = "username") username: String, @PathVariable(value = "isbn") isbn: String, authentication: Authentication) {
		logger.info("renewBook $username, $isbn")
		validateUser(username, authentication)
		service.renewBook(username, isbn) ?: throw NoResourceFoundException(HttpMethod.PATCH, "/checkouts/${username}/${isbn}/return")
	}
}
