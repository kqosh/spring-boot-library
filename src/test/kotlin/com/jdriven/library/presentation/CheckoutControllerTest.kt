package com.jdriven.library.presentation

import com.jdriven.library.service.model.Checkout
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
import kotlin.test.assertContains

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

		val checkouts: List<Checkout> = findCheckouts(username, 200)

		assertEquals(2, checkouts.size)

		val checkoutsByIsbn: Map<String, Checkout> = checkouts.associateBy { it.book.isbn }
		assertEquals(LocalDate.of(2025, 7, 8), checkoutsByIsbn["isbn123"]!!.checkoutAt)
		assertEquals(LocalDate.of(2025, 7, 15), checkoutsByIsbn["isbn124"]!!.checkoutAt)
	}

	private fun findCheckouts(username: String, expectedStatusCode: Int): List<Checkout> {
		return get(username, expectedStatusCode, username, "pwuser").`as`(object : TypeRef<List<Checkout>>() {})
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
		assertTrue(get(username, 403, "user102", "pwuser").asString().contains("other user not allowed"))//qqqq add more errorbody asserts
	}

	@Test
	fun findByUsername_notFound() {
		val username = "Doesnt Exist"
		get(username, 404, "admin", "pwadmin")
	}

	@Test
	fun createFindRenewReturn() {
		val username = "user102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${username}/${isbn}"
		run {
			builder(baseUrl, username, isbn, 201).post()

			val checkouts = findCheckouts(username, 200)
			assertEquals(1, checkouts.size, checkouts.toString())
			assertEquals(0, checkouts[0].renewCount, checkouts[0].toString())
		}
		run {
			builder("${baseUrl}/renew", username, isbn, 200).patch()

			val checkouts = findCheckouts(username, 200)
			assertEquals(1, checkouts.size, checkouts.toString())
			assertEquals(1, checkouts[0].renewCount, checkouts[0].toString())

			val rsp = builder("${baseUrl}/renew", username, isbn, 400).patch().asString()
			assertTrue(rsp.contains("exceeded"))
		}
		run {
			builder("${baseUrl}/return", username, isbn, 200).patch()

			val checkouts = findCheckouts(username, 200)
			assertEquals(0, checkouts.size, checkouts.toString())
		}
	}

	private fun builder(url: String, username: String, isbn: String, expectedStatusCode: Int, loginUserName: String = username): RestCallBuilder =
		RestCallBuilder(url, expectedStatusCode).username(loginUserName).password("pwuser")

	@Test
	fun create_userNotFound() {
		val nr = "user102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${nr}/${isbn}"
		RestCallBuilder(baseUrl, 201).username("user102").password("pwuser").post()
	}

	@Test
	fun create_bookNotFound() {
		val nr = "user102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${nr}/${isbn}"
		RestCallBuilder(baseUrl, 201).username("user102").password("pwuser").post()
	}

	@Test
	fun create_bookNotAvailable() {
		val nr = "user102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${nr}/${isbn}"
		RestCallBuilder(baseUrl, 201).username("user102").password("pwuser").post()
	}

	//qqqq create, return renew other user not allowed
}
