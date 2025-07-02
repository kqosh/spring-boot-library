package com.jdriven.library.presentation

import com.jdriven.library.access.model.BookEntity
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
		val isbn = "123"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/books/${isbn}", BookEntity::class.java)
		Assertions.assertEquals(200, rsp.statusCode.value(), rsp.toString())
		Assertions.assertEquals(isbn, rsp.body!!.isbn, rsp.toString())
	}

	@Test
	fun findIsbn_notFound() {
		val isbn = "123NotFound"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/books/${isbn}", String::class.java)
		Assertions.assertEquals(404, rsp.statusCode.value(), rsp.toString())
		Assertions.assertTrue(rsp.body!!.contains(isbn), rsp.toString())
	}
}
