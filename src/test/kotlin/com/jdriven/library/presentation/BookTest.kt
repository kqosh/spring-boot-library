package com.jdriven.library.presentation

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookTest() {

	@LocalServerPort
	private var port: Int? = null

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Test
	fun findIsbn_found() {
		findIsbn( "123", 200)
	}

	@Test
	fun findIsbn_notFound() {
		findIsbn( "123NotFound", 404)
	}

	private fun findIsbn(isbn: String, expectedStatus: Int) {
		val rsp = get(isbn)
		Assertions.assertEquals(expectedStatus, rsp.statusCode.value(), rsp.toString())
		Assertions.assertTrue(rsp.body!!.contains(isbn), rsp.toString())
	}

	private fun get(isbn: String): ResponseEntity<String> {//qqqq Book
		return restTemplate.getForEntity("http://localhost:${port}/books/${isbn}", String::class.java)
	}
}
