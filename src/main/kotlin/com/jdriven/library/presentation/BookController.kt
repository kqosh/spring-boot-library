package com.jdriven.library.presentation

import com.jdriven.library.service.BookService
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/books")
class BookController(private val service: BookService) {

	@GetMapping("/{isbn}")
	fun greeting(@PathVariable(value = "isbn") isbn: String): String {
		return service.find(isbn) ?: throw NoResourceFoundException(HttpMethod.GET, "/books/{isbn}")
	}

	//qqqq add (create), update, delete book
	//qqqq find by title xor author or both
	//qqqq borrow xor return book
}
