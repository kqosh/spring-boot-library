package com.jdriven.library.presentation

import com.jdriven.library.service.model.Checkout
import io.restassured.RestAssured
import io.restassured.common.mapper.TypeRef
import io.restassured.response.ResponseBodyExtractionOptions
import org.junit.jupiter.api.Assertions.assertEquals
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
	fun findByMemberNumber() {
		val nr = "nr101"

		val checkouts: List<Checkout> = findCheckouts(nr, 200)

		assertEquals(2, checkouts.size)

		val checkoutsByIsbn: Map<String, Checkout> = checkouts.associateBy { it.book.isbn }
		assertEquals(LocalDate.of(2025, 7, 8), checkoutsByIsbn["isbn123"]!!.checkoutAt)
		assertEquals(LocalDate.of(2025, 7, 15), checkoutsByIsbn["isbn124"]!!.checkoutAt)
	}

	private fun findCheckouts(memberNr: String, expectedStatusCode: Int): List<Checkout> {
		return get(memberNr, expectedStatusCode).`as`(object : TypeRef<List<Checkout>>() {})
	}

	private fun get(memberNr: String, expectedStatusCode: Int, userId: String = memberNr, password: String = "pwuser"): ResponseBodyExtractionOptions {
		return RestCallBuilder("http://localhost:${port}/checkouts/${memberNr}", expectedStatusCode)
			.username(userId)
			.password(password)
			.get()
	}

	@Test
	fun findByMemberNumber_noAccess() {
		val nr = "Doesnt Exist"
		get(nr, 401)
	}

	@Test
	fun findByMemberNumber_notFound() {
		val nr = "Doesnt Exist"
		get(nr, 404, "nr101")
	}

	@Test
	fun createFindReturn() {
		val nr = "nr102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${nr}/${isbn}"
		run {
			val createRsp = RestCallBuilder(baseUrl, 201).username("nr102").password("pwuser").post()
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
}
