package com.jdriven.library.presentation

import com.jdriven.library.service.model.Book
import org.junit.jupiter.api.Assertions
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

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Test
	fun findIsbn_found() {
		val isbn = "isbn123"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/books/${isbn}", Book::class.java)
		Assertions.assertEquals(200, rsp.statusCode.value(), rsp.toString())
		Assertions.assertEquals(isbn, rsp.body!!.isbn, rsp.toString())
		Assertions.assertEquals("Jan Klaassen", rsp.body!!.authorName, rsp.toString())
	}

	@Test
	fun findIsbn_notFound() {
		val isbn = "isbnNotFound"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/books/${isbn}", String::class.java)
		Assertions.assertEquals(404, rsp.statusCode.value(), rsp.toString())
		Assertions.assertTrue(rsp.body!!.contains(isbn), rsp.toString())
	}

	//qqqq test create, delete, borrow, return
}
