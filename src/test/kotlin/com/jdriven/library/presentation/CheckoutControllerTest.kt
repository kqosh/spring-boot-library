package com.jdriven.library.presentation

import com.jdriven.library.service.model.CheckoutDto
import io.restassured.RestAssured
import io.restassured.common.mapper.TypeRef
import io.restassured.response.ResponseBodyExtractionOptions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.LocalDate

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CheckoutControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	@Test
	fun findByUsername() {
		val username = "user101"

		val checkouts: List<CheckoutDto> = findCheckouts(username, 200)

		assertEquals(2, checkouts.size)

		val checkoutsByIsbn: Map<String, CheckoutDto> = checkouts.associateBy { it.book.isbn }
		assertEquals(LocalDate.of(2025, 7, 8), checkoutsByIsbn["isbn123"]!!.checkoutAt.toLocalDate())
		assertEquals(LocalDate.of(2025, 7, 15), checkoutsByIsbn["isbn124"]!!.checkoutAt.toLocalDate())
		//qqqq due dates
	}

	private fun findCheckouts(username: String, expectedStatusCode: Int, loginUsername: String = username, password: String = "pwuser"): List<CheckoutDto> {
		return get(username, expectedStatusCode, loginUsername, password).`as`(object : TypeRef<List<CheckoutDto>>() {})
	}

	private fun get(username: String, expectedStatusCode: Int, loginUsername: String, password: String): ResponseBodyExtractionOptions {
		return RestCallBuilder("http://localhost:${port}/checkouts/${username}", expectedStatusCode)
			.username(loginUsername)
			.password(password)
			.get()
	}

	@Test
	fun findByUsername_noAccess() {
		val username = "Doesnt Exist"
		get(username, 401, username, "pwuser")
	}

	@Test
	fun findByUsername_otherUserNotAllowed() {
		val username = "user101"
		assertTrue(
			get(username, 403, "user102", "pwuser").asString().contains("other user not allowed")
		)
	}

	@Test
	fun findByUsername_notFound() {
		val username = "Doesnt Exist"
		assertTrue(get(username, 400, "admin", "pwadmin").asString().contains("user not found: $username"))
	}

	@Test
	fun findByUsernameAsAdmin() {
		val username = "user101"
		val checkouts = findCheckouts(username, 200, "admin", "pwadmin")
		assertEquals(2, checkouts.size)
		val expectedIsbns = listOf("isbn123", "isbn124")
		checkouts.forEach { assertTrue(it.book.isbn in expectedIsbns) }
	}

	@Test
	fun createFindRenewReturn() {
		val username = "user102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${username}/${isbn}"
		run {
			builder(baseUrl, 201).post()

			val checkouts = findCheckouts(username, 200)
			assertEquals(1, checkouts.size, checkouts.toString())
			assertEquals(0, checkouts[0].renewCount, checkouts[0].toString())
		}
		run {
			builder("${baseUrl}/renew", 200).patch()

			val checkouts = findCheckouts(username, 200)
			assertEquals(1, checkouts.size, checkouts.toString())
			assertEquals(1, checkouts[0].renewCount, checkouts[0].toString())

			val rsp = builder("${baseUrl}/renew", 400).patch().asString()
			assertTrue(rsp.contains("max renew count (1) exceeded"))
		}
		run {
			builder("${baseUrl}/return", 200).patch()

			val checkouts = findCheckouts(username, 200)
			assertEquals(0, checkouts.size, checkouts.toString())
		}
	}

	private fun builder(url: String, expectedStatusCode: Int, loginUserName: String = "user102", password: String = "pwuser"): RestCallBuilder =
		RestCallBuilder(url, expectedStatusCode).username(loginUserName).password(password)

	@Test
	fun create_userNotFound() {
		val username = "user123"
		val isbn = "isbn101"
		val rsp = builder("http://localhost:${port}/checkouts/${username}/${isbn}", 400, "admin", "pwadmin").post().asString()
		assertTrue(rsp.contains("user not found: $username"))
	}

	@Test
	fun create_bookNotFound() {
		val username = "user102"
		val isbn = "isnotthere"
		val rsp = builder("http://localhost:${port}/checkouts/${username}/${isbn}", 400).post().asString()
		assertTrue(rsp.contains("book not found: $isbn"))
	}

	@Test
	fun create_bookNotAvailable() {
		val username = "user102"
		val isbn = "isbn125"
		val rsp = builder("http://localhost:${port}/checkouts/${username}/${isbn}", 409).post().asString()
		assertTrue(rsp.contains("currently no books available for: isbn125"))
	}

	@Test
	fun create_otherUserNotAllowed() {
		val username = "user101"
		val isbn = "isbn123"
		val rsp = builder("http://localhost:${port}/checkouts/${username}/${isbn}", 403).post().asString()
		assertTrue(rsp.contains("other user not allowed"))
	}

	//qqqq create, return renew other user not allowed, not found
}
