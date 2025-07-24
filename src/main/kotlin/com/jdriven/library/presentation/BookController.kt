package com.jdriven.library.presentation

import com.jdriven.library.service.BookService
import com.jdriven.library.service.model.BookDto
import com.jdriven.library.service.model.PaginatedResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService) {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@Operation(summary = "Find book by ISBN.") //qqqq Nx
	@GetMapping("/{isbn}")
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun findByIsbn(@PathVariable(value = "isbn") isbn: String): BookDto {
		logger.info("findByIsbn $isbn")
		return service.find(isbn) ?: throw NoResourceFoundException(HttpMethod.GET, "/books/${isbn}")
	}

	@Operation(summary = "Create a new book.")
	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	@ApiResponse(responseCode = "201", description = "Created")
	@ApiStandardErrors
	fun create(@RequestBody book: BookDto) {
		logger.info("create $book")
		service.create(book) ?: throw NoResourceFoundException(HttpMethod.POST, "/books")
	}

	@Operation(summary = "Update an existing book.")
	@PutMapping("/{isbn}")
	@PreAuthorize("hasRole('ADMIN')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun update(@PathVariable(value = "isbn") isbn: String, @RequestBody book: BookDto) {
		logger.info("update $isbn, $book")
		service.update(isbn, book) ?: throw NoResourceFoundException(HttpMethod.PUT, "/books/${book.isbn}")
	}

	@Operation(summary = "Delete a book.")
	@DeleteMapping("/{isbn}")
	@PreAuthorize("hasRole('ADMIN')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun delete(@PathVariable(value = "isbn") isbn: String): BookDto {
		logger.info("delete $isbn")
		return service.delete(isbn) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/books/${isbn}")
	}

	@Operation(summary = "Search case insensitively books on whole words that appear in the author name and/or the title.")
	@GetMapping("/search")
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun search(
		@RequestParam(required = false, defaultValue = "") author: String?,
		@RequestParam(required = false, defaultValue = "") title: String?,
		@RequestParam(required = false, defaultValue = "0") page: String?,
		@RequestParam(required = false, defaultValue = "20") size: String?
	): PaginatedResponse<BookDto> {
		logger.info("search $author - $title, page=$page, size=$size")
		return service.search(author, title, page!!.toInt(), size!!.toInt(), false)
	}

	@Operation(summary = "Search case insensitively for books whose author name and/or title starts with the specified search term(s).")
	@GetMapping("/search-starts-with")
	@PreAuthorize("hasRole('USER')")
	@ApiResponse(responseCode = "200", description = "OK")
	@ApiStandardErrors
	fun searchWithSql(
		@RequestParam(required = false, defaultValue = "") author: String?,
		@RequestParam(required = false, defaultValue = "") title: String?,
		@RequestParam(required = false, defaultValue = "0") page: String?,
		@RequestParam(required = false, defaultValue = "20") size: String?
	): PaginatedResponse<BookDto> {
		logger.info("search $author - $title, page=$page, size=$size")
		return service.search(author, title, page!!.toInt(), size!!.toInt(), true)
	}
}
