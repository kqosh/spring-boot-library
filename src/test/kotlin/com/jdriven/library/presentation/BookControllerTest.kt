package com.jdriven.library.presentation

import com.jdriven.library.service.model.Book
import io.restassured.RestAssured
import io.restassured.common.mapper.TypeRef
import io.restassured.response.ResponseBodyExtractionOptions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	private fun findByIsbn(isbn: String, expectedStatusCode: Int): ResponseBodyExtractionOptions =
		RestCallBuilder("http://localhost:${port}/books/${isbn}", expectedStatusCode).username("user101").password("pwuser").get()

	@Test
	fun findIsbn_found() {
		val isbn = "isbn123"
		val book = findByIsbn(isbn, 200).`as`(Book::class.java)!!
		Assertions.assertEquals(isbn, book.isbn)
		Assertions.assertEquals("Jan Klaassen", book.authorName)
	}

	@Test
	fun findIsbn_notFound() {
		val isbn = "isbnNotFound"
		val body = findByIsbn(isbn, 404).asString()
		Assertions.assertTrue(body.contains(isbn), body)
	}

	@Test
	fun search_byAuthor() {
		val books = searchAsBooks("RENE", null, 200)
		Assertions.assertEquals(1, books.size)
		Assertions.assertEquals("Rene Goscinny", books[0].authorName)
	}

	@Test
	fun search_byAuthorNotFound() {
		val books = searchAsBooks("HARRY", null, 200)
		Assertions.assertEquals(0, books.size)
//		Assertions.assertEquals("Rene Goscinny", books[0].authorName)
	}

	@Test
	fun search_NoArgs() {
		val books = searchAsRspOptions("", null, 400)
//		Assertions.assertEquals(0, books.size)
////		Assertions.assertEquals("Rene Goscinny", books[0].authorName)
	}

	@Test
	fun search_byTitle() {
		val books = searchAsBooks(null, "DE POP", 200)
		Assertions.assertEquals(3, books.size)
		books.forEach { Assertions.assertTrue(it.title!!.startsWith("De poppenkast")) }
	}

	@Test
	fun search_byAuthorAndTitle() {
		val books = searchAsBooks("jan", "de poppenkast", 200)
		Assertions.assertEquals(2, books.size)
		books.forEach { Assertions.assertTrue(it.title!!.startsWith("De poppenkast")) }
	}

	private fun searchAsBooks(author: String?, title: String?, expectedStatusCode: Int): List<Book> =
		searchAsRspOptions(author, title, expectedStatusCode).`as`(object : TypeRef<List<Book>>() {})

	private fun searchAsRspOptions(author: String?, title: String?, expectedStatusCode: Int): ResponseBodyExtractionOptions {
		var url = "http://localhost:${port}/books/search"
		var separator = "?"
		if (author != null) {
			url += "${separator}author=${author}"
			separator = "&"
		}
		if (title != null) url += "${separator}title=${title}"
		return RestCallBuilder(url, expectedStatusCode).username("user101").password("pwuser").get()
	}

	@Test
	fun createFindDelete() {
		val baseUrl = "http://localhost:${port}/books"
		val isbn = "isbn999"
		run {
			RestCallBuilder(baseUrl, 201).body(Book(isbn, "an author", "a title", numberOfCopies = 4)).username("admin").password("pwadmin").post()
		}
		run {
			findByIsbn(isbn, 200)
		}
		run {
			RestCallBuilder("${baseUrl}/${isbn}", 200).username("admin").password("pwadmin").delete()

			findByIsbn(isbn, 404)
		}
	}
}
