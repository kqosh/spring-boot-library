package com.jdriven.library.presentation

import com.jdriven.library.access.model.BookEntity
import com.jdriven.library.presentation.model.Book
import com.jdriven.library.service.BookService
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService) {

	@GetMapping("/{isbn}")
	fun findByIsbn(@PathVariable(value = "isbn") isbn: String): Book {
		return service.find(isbn)?.let { Book.of(it) } ?: throw NoResourceFoundException(HttpMethod.GET, "/books/${isbn}")
	}
//
//	@PostMapping("/")
//	fun create(@RequestBody book: Book) {
//		return service.create(book.toEntity())
//	}qqqq

	//qqqq add (create), update, delete book
	//qqqq find by title xor author or both
	//qqqq borrow xor return book
}
