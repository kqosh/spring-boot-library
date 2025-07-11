package com.jdriven.library.presentation

import com.jdriven.library.service.CheckoutService
import com.jdriven.library.service.model.Checkout
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/checkouts")
class CheckoutController(private val service: CheckoutService) {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@GetMapping("/{username}")
	@PreAuthorize("hasRole('USER')")
	fun findByUsername(@PathVariable(value = "username") username: String): List<Checkout> {
		return service.findByUsername(username) ?: throw NoResourceFoundException(HttpMethod.GET, "/checkouts/${username}")
	}

	//qqqq find by book

	@PostMapping("{username}/{isbn}")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('USER')")
	fun create(@PathVariable(value = "username") username: String, @PathVariable(value = "isbn") isbn: String) {
		logger.info("create $username, $isbn")
		service.create(username, isbn) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/checkouts/${username}/${isbn}")
	}

	@PatchMapping("{username}/{isbn}/return")
	@PreAuthorize("hasRole('USER')")
	fun returnBook(@PathVariable(value = "username") username: String, @PathVariable(value = "isbn") isbn: String) {
		service.returnBook(username, isbn) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/checkouts/${username}/${isbn}/return")
	}
}
