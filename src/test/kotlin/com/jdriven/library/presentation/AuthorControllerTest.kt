package com.jdriven.library.presentation

import com.jdriven.library.presentation.model.Author
import com.jdriven.library.presentation.model.CreateAuthorRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthorControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Test
	fun findByName_found() {
		val name = "Jan Klaassen"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/authors/${name}", Author::class.java)
		assertEquals(200, rsp.statusCode.value(), rsp.toString())
		val author = rsp.body!!
		assertEquals(name, author.name, rsp.toString())
		assertEquals(2, author.books.size, rsp.toString())
		val isbns = author.books.map { b -> b.isbn }
		assertTrue(isbns.contains("isbn123"), rsp.toString())
		assertTrue(isbns.contains("isbn124"), rsp.toString())
	}

	@Test
	fun findByName_notFound() {
		val name = "Doesnt Exist"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/authors/${name}", String::class.java)
		assertEquals(404, rsp.statusCode.value(), rsp.toString())
		assertTrue(rsp.body!!.contains(name.replace(" ", "%20")), rsp.toString())
	}

	@Test
	fun createFindDelete() {
		val baseUrl = "http://localhost:${port}/authors"

		val name = "Jan"
		val createRequest = CreateAuthorRequest( name)

		val createRsp = restTemplate.postForEntity<String?>(baseUrl, createRequest, String::class.java)
		assertEquals(201, createRsp.statusCode.value(), createRsp.toString())

		val findRsp1 = restTemplate.getForEntity("${baseUrl}/${name}", Author::class.java)
		assertEquals(200, findRsp1.statusCode.value(), findRsp1.toString())

		restTemplate.delete("${baseUrl}/${name}", createRequest, String::class.java)

		val findRsp2 = restTemplate.getForEntity("${baseUrl}/${name}", String::class.java)
		assertEquals(404, findRsp2.statusCode.value(), findRsp2.toString())
	}
}
