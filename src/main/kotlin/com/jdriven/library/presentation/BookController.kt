package com.jdriven.library.presentation

import com.jdriven.library.service.BookService
import com.jdriven.library.service.model.Book
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService) {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@GetMapping("/{isbn}")
	@PreAuthorize("hasRole('USER')")
	fun findByIsbn(@PathVariable(value = "isbn") isbn: String): Book {
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
		logger.info("update $isbn, $book")//qqqq more logging
		if (isbn != book.isbn) throw HttpClientErrorException(HttpStatusCode.valueOf(400))
		service.update(book) ?: throw NoResourceFoundException(HttpMethod.PUT, "/books/${book.isbn}")
	}

	@DeleteMapping("/{isbn}")
	@PreAuthorize("hasRole('ADMIN')")
	fun delete(@PathVariable(value = "isbn") isbn: String): Book {
		logger.info("delete $isbn")
		return service.delete(isbn) ?: throw NoResourceFoundException(HttpMethod.DELETE, "/books/${isbn}")
	}



	//qqqq update, delete book
	//qqqq find by part of title xor author or both
}
