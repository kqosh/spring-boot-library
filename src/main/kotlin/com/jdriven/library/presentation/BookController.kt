package com.jdriven.library.presentation

import com.jdriven.library.service.BookService
import com.jdriven.library.service.model.Book
import com.jdriven.library.service.model.PaginatedResponse
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

	@GetMapping("/{isbn}")
	@PreAuthorize("hasRole('USER')")
	fun findByIsbn(@PathVariable(value = "isbn") isbn: String): Book {
		logger.info("findByIsbn $isbn")
		return service.find(isbn) ?: throw NoResourceFoundException(HttpMethod.GET, "/books/${isbn}")
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	fun create(@RequestBody book: Book) {
		logger.info("create $book")
		service.create(book) ?: throw NoResourceFoundException(HttpMethod.POST, "/books")
	}

	@PutMapping("/{isbn}")
	@PreAuthorize("hasRole('ADMIN')")
	fun update(@PathVariable(value = "isbn") isbn: String, @RequestBody book: Book) {
		logger.info("update $isbn, $book")
		service.update(isbn, book) ?: throw NoResourceFoundException(HttpMethod.PUT, "/books/${book.isbn}")
	}

	@DeleteMapping("/{isbn}")
	@PreAuthorize("hasRole('ADMIN')")
	fun delete(@PathVariable(value = "isbn") isbn: String): Book {
		logger.info("delete $isbn")
		return service.delete(isbn) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/books/${isbn}")
	}

	@GetMapping("/search")
	@PreAuthorize("hasRole('USER')")
	fun search(
		@RequestParam(required = false, defaultValue = "") author: String?,
		@RequestParam(required = false, defaultValue = "") title: String?,
		@RequestParam(required = false, defaultValue = "0") page: String?,
		@RequestParam(required = false, defaultValue = "20") size: String?
	): PaginatedResponse<Book> {
		logger.info("search $author - $title, page=$page, size=$size")
		try {
			return service.search(author, title, page!!.toInt(), size!!.toInt())
		} catch (ex: Exception) {
			throw RestCallUtils.translateException(ex)
		}
	}
}
