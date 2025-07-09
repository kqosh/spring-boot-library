package com.jdriven.library.presentation

import com.jdriven.library.service.model.Author
import com.jdriven.library.service.model.Book
import com.jdriven.library.service.model.CreateAuthorRequest
import io.restassured.RestAssured
import io.restassured.response.ResponseBodyExtractionOptions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
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

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	private fun findByName(name: String, expectedStatusCode: Int): ResponseBodyExtractionOptions {
		return RestAssuredUtils.get("http://localhost:${port}/authors/${name}", expectedStatusCode, "admin", "pwadmin")
	}

	@Test
	fun findByName_found() {
		val name = "Jan Klaassen"
		val author = findByName(name, 200).`as`(Author::class.java)!!

//		val rsp = restTemplate.getForEntity("http://localhost:${port}/authors/${name}", Author::class.java)
//		assertEquals(200, rsp.statusCode.value(), rsp.toString())
//		val author = rsp.body!!
		assertEquals(name, author.name)
		assertEquals(2, author.books.size)
		val isbns = author.books.map { b -> b.isbn }
		assertTrue(isbns.contains("isbn123"))
		assertTrue(isbns.contains("isbn124"))
	}

	@Test
	fun findByName_notFound() {
		val name = "Doesnt Exist"
		val rsp = findByName(name, 404).asString()//restTemplate.getForEntity("http://localhost:${port}/authors/${name}", String::class.java)
		assertTrue(rsp.contains(name.replace(" ", "%20")), rsp.toString())
	}

	@Test
	fun createFindDelete() {
		val baseUrl = "http://localhost:${port}/authors"
		val name = "Klaas"
		run {
			RestCallBuilder(baseUrl, 201).body(CreateAuthorRequest(name)).username("admin").password("pwadmin").post()
//			val createRequest = CreateAuthorRequest(name)
//			val createRsp = restTemplate.postForEntity(baseUrl, createRequest, String::class.java)
//			assertEquals(201, createRsp.statusCode.value(), createRsp.toString())qqqq
		}
		run {
			findByName(name, 200)
//			val findRsp1 = restTemplate.getForEntity("${baseUrl}/${name}", Author::class.java)
//			assertEquals(200, findRsp1.statusCode.value(), findRsp1.toString())qqqq
		}
		run {
			RestCallBuilder("${baseUrl}/${name}", 200).username("admin").password("pwadmin").delete()
//			restTemplate.delete("${baseUrl}/${name}", null, String::class.java)qqqq

			findByName(name, 404)
//			val findRsp2 = restTemplate.getForEntity("${baseUrl}/${name}", String::class.java)
//			assertEquals(404, findRsp2.statusCode.value(), findRsp2.toString())qqqq
		}
	}
}
