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

	@GetMapping("/{memberNumber}")
	@PreAuthorize("hasRole('USER')")
	fun findByMember(@PathVariable(value = "memberNumber") memberNumber: String): List<Checkout> {
		return service.findByMember(memberNumber) ?: throw NoResourceFoundException(HttpMethod.GET, "/checkouts/${memberNumber}")
	}

	//qqqq find by book

	@PostMapping("{memberNumber}/{isbn}")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('USER')")
	fun create(@PathVariable(value = "memberNumber") memberNumber: String, @PathVariable(value = "isbn") isbn: String) {
		logger.info("create $memberNumber, $isbn")
		service.create(memberNumber, isbn) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/checkouts/${memberNumber}/${isbn}")
	}

	@PatchMapping("{memberNumber}/{isbn}/return")
	@PreAuthorize("hasRole('USER')")
	fun returnBook(@PathVariable(value = "memberNumber") memberNumber: String, @PathVariable(value = "isbn") isbn: String) {
		service.returnBook(memberNumber, isbn) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/checkouts/${memberNumber}/${isbn}/return")
	}
}
