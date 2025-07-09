package com.jdriven.library.presentation

import com.jdriven.library.service.model.Book
import io.restassured.RestAssured
import io.restassured.RestAssured.given
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
		RestAssuredUtils.get("http://localhost:${port}/books/${isbn}", expectedStatusCode, "nr101")

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

	//qqqq test create, delete
}
