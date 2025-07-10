package com.jdriven.library.presentation

import com.jdriven.library.service.model.Author
import com.jdriven.library.service.model.CreateAuthorRequest
import io.restassured.RestAssured
import io.restassured.response.ResponseBodyExtractionOptions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthorControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	private fun findByName(name: String, expectedStatusCode: Int, userId: String = "admin", password: String = "pwadmin"): ResponseBodyExtractionOptions {
		return RestAssuredUtils.get("http://localhost:${port}/authors/${name}", expectedStatusCode, userId, password)
	}

	@Test
	fun findByName_found() {
		val name = "Jan Klaassen"
		val author = findByName(name, 200).`as`(Author::class.java)!!

		assertEquals(name, author.name)
		assertEquals(2, author.books.size)
		val isbns = author.books.map { b -> b.isbn }
		assertTrue(isbns.contains("isbn123"))
		assertTrue(isbns.contains("isbn124"))
	}

	@Test
	fun findByName_userDoesNotExist() {
		val name = "Jan Klaassen"
		findByName(name, 401, "nr013", "pwuser").asString()
	}

	@Test
	fun findByName_wrongPassword() {
		val name = "Jan Klaassen"
		findByName(name, 401, "nr101", "wrong-pw").asString()
	}

	@Test
	fun findByName_notFound() {
		val name = "Doesnt Exist"
		val rsp = findByName(name, 404).asString()
		assertTrue(rsp.contains(name.replace(" ", "%20")), rsp.toString())
	}

	@Test
	fun createFindDelete() {
		val baseUrl = "http://localhost:${port}/authors"
		val name = "Klaas"
		run {
			RestCallBuilder(baseUrl, 201).body(CreateAuthorRequest(name)).username("admin").password("pwadmin").post()
		}
		run {
			findByName(name, 200)
		}
		run {
			RestCallBuilder("${baseUrl}/${name}", 200).username("admin").password("pwadmin").delete()

			findByName(name, 404)
		}
	}

	@Test
	fun createNotAllowed() {
		val baseUrl = "http://localhost:${port}/authors"
		val name = "Henk"
		run {
			RestCallBuilder(baseUrl, 403).body(CreateAuthorRequest(name)).username("nr101").password("pwuser").post()
		}
	}
}
