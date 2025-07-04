package com.jdriven.library.presentation

import com.jdriven.library.service.BookService
import com.jdriven.library.service.model.Book
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService) {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@GetMapping("/{isbn}")
	fun findByIsbn(@PathVariable(value = "isbn") isbn: String): Book {
		return service.find(isbn) ?: throw NoResourceFoundException(HttpMethod.GET, "/books/${isbn}")
	}


	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	fun create(@RequestBody book: Book) {
		logger.info("create $book")
		service.create(book) ?: throw NoResourceFoundException(HttpMethod.GET, "/authors/${book.authorName}")
	}

	@PatchMapping("/{isbn}/borrow/{memberNumber}")
	fun borrow(@PathVariable(value = "isbn") isbn: String, @PathVariable(value = "memberNumber") memberNumber: String) {
		//qqqq
	}

	@PatchMapping("/{isbn}/return")
	fun returnBook(@PathVariable(value = "isbn") isbn: String) {
		//qqqq
	}

	//qqqq add (create), update, delete book
	//qqqq find by title xor author or both
}
