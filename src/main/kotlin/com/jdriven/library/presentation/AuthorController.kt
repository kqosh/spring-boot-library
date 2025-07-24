package com.jdriven.library.presentation

import com.jdriven.library.service.AuthorService
import com.jdriven.library.service.model.AuthorDto
import com.jdriven.library.service.model.CreateOrUpdateAuthorRequest
import com.jdriven.library.service.model.PaginatedResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/authors")
class AuthorController(private val service: AuthorService) {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@GetMapping("/{name}")
	@PreAuthorize("hasRole('USER')")
	fun findByName(@PathVariable(value = "name") name: String): AuthorDto {
		logger.info("findByName $name")
		return service.find(name) ?: throw NoResourceFoundException(HttpMethod.GET, "/authors/${name}")
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	fun create(@RequestBody request: CreateOrUpdateAuthorRequest) {
		logger.info("create $request")
		service.create(request)
	}

	@DeleteMapping("/{name}")
	@PreAuthorize("hasRole('ADMIN')")
	fun delete(@PathVariable(value = "name") name: String) {
		logger.info("deleteByName $name")
		service.delete(name) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/authors/${name}")
	}

	@GetMapping("/search-starts-with")
	@PreAuthorize("hasRole('USER')")
	fun search(
		@RequestParam(required = false, defaultValue = "") name: String?,
		@RequestParam(required = false, defaultValue = "0") page: String?,
		@RequestParam(required = false, defaultValue = "20") size: String?
	): PaginatedResponse<AuthorDto> {
		logger.info("search $name, page=$page, size=$size")
		return service.search(name, page!!.toInt(), size!!.toInt())
	}
}
