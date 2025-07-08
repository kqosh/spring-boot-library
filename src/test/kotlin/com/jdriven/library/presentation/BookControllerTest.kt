package com.jdriven.library.presentation

import com.jdriven.library.service.model.Book
import com.jdriven.library.service.model.Checkout
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.common.mapper.TypeRef
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookControllerTest() {

	@LocalServerPort
	private var port: Int? = null
//qqqq
//	@Autowired
//	private lateinit var restTemplate: TestRestTemplate

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	private fun <T> findByIsbn(isbn: String, expectedStatusCode: Int, responseType: Class<T>): T? {//qqqq to util object
		return given()
			.log().all()
			. auth().basic("user", "pwuser")
			.`when`().get("http://localhost:${port}/books/${isbn}")
			.then()
			.log().all()
			.statusCode(expectedStatusCode)
			.extract().body().`as`(responseType)
	}

	@Test
	fun findIsbn_found() {
		val isbn = "isbn123"
		val book = findByIsbn(isbn, 200, Book::class.java)!!
		Assertions.assertEquals(isbn, book.isbn)
		Assertions.assertEquals("Jan Klaassen", book.authorName)
//		val rsp = restTemplate.getForEntity("http://localhost:${port}/books/${isbn}", Book::class.java)
//		Assertions.assertEquals(200, rsp.statusCode.value(), rsp.toString())
//		Assertions.assertEquals(isbn, rsp.body!!.isbn, rsp.toString())
//		Assertions.assertEquals("Jan Klaassen", rsp.body!!.authorName, rsp.toString())
	}

	@Test
	fun findIsbn_notFound() {
		val isbn = "isbnNotFound"

		val body = given()
			.log().all()
			. auth().basic("user", "pwuser")
			.`when`().get("http://localhost:${port}/books/${isbn}")
			.then()
			.log().all()
			.statusCode(404)
			.extract().body().asString()


//		val body = findByIsbn(isbn, 404, String::class.java)!!qqqq
		Assertions.assertTrue(body.contains(isbn), body)
//		val rsp = restTemplate.getForEntity("http://localhost:${port}/books/${isbn}", String::class.java)
//		Assertions.assertEquals(404, rsp.statusCode.value(), rsp.toString())
//		Assertions.assertTrue(rsp.body!!.contains(isbn), rsp.toString())
	}

	//qqqq test create, delete
}
