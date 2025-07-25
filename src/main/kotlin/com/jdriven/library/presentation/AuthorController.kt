package com.jdriven.library.presentation

import com.jdriven.library.service.AuthorService
import com.jdriven.library.service.model.AuthorDto
import com.jdriven.library.service.model.CreateOrUpdateAuthorRequest
import com.jdriven.library.service.model.PaginatedResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
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

	@Operation(summary = "Find an author by name.")
	@GetMapping("/{name}")
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun findByName(@PathVariable(value = "name") name: String): AuthorDto {
		logger.info("findByName $name")
		return service.find(name) ?: throw NoResourceFoundException(HttpMethod.GET, "/authors/${name}")
	}

	@Operation(summary = "Create a new author.")
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	@ApiResponse(responseCode = "201", description = "Created")
	@ApiStandardErrors
	fun create(@RequestBody request: CreateOrUpdateAuthorRequest) {
		logger.info("create $request")
		service.create(request)
	}

	@Operation(summary = "Delete an author by name.")
	@DeleteMapping("/{name}")
	@PreAuthorize("hasRole('ADMIN')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun delete(@PathVariable(value = "name") name: String) {
		logger.info("deleteByName $name")
		service.delete(name) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/authors/${name}")
	}

	@Operation(summary = "Search case insensitively for authors with a name that starts with the specified search term.")
	@GetMapping("/search-starts-with")
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun search(
		@RequestParam(required = false, defaultValue = "") name: String?,
		@RequestParam(required = false, defaultValue = "0") page: String?,
		@RequestParam(required = false, defaultValue = "20") size: String?
	): PaginatedResponse<AuthorDto> {
		logger.info("search $name, page=$page, size=$size")
		return service.search(name, page!!.toInt(), size!!.toInt())
	}
}
