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
		val username = "nr101"

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
		val username = "nr101" //qqqq replace all nr10 -> user10
		assertTrue(get(username, 403, "nr102", "pwuser").asString().contains("other user not allowed"))//qqqq add more errorbody asserts
	}

	@Test
	fun findByUsername_notFound() {
		val username = "Doesnt Exist"
		get(username, 404, "admin", "pwadmin")
	}

	@Test
	fun createFindReturn() {
		val nr = "nr102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${nr}/${isbn}"
		run {
			RestCallBuilder(baseUrl, 201).username("nr102").password("pwuser").post()
		}
		run {
			val checkouts = findCheckouts(nr, 200)
			assertEquals(1, checkouts.size, checkouts.toString())
		}
		run {
			RestCallBuilder("${baseUrl}/return", 200).username("nr102").password("pwuser").patch()

			val checkouts = findCheckouts(nr, 200)
			assertEquals(0, checkouts.size, checkouts.toString())
		}
	}

	//qqqq create user not found
	//qqqq get, create otheruser notallowed
	//qqqq book not found
	//qqqq book not available
}
