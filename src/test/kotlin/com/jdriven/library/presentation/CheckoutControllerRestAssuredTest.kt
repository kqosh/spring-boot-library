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
class CheckoutControllerRestAssuredTest() {

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

	private fun get(memberNr: String, expectedStatusCode: Int): ResponseBodyExtractionOptions {
		return RestCallBuilder("http://localhost:${port}/checkouts/${memberNr}", expectedStatusCode)
			.username(memberNr)
			.password("pwuser")
			.get()
	}

	@Test
	fun findByMemberNumber_notFound() {
		val nr = "Doesnt Exist"
		get(nr, 401)
	}

	@Test
	fun createFindReturn() {
		val nr = "nr102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${nr}/${isbn}"
// qqqq convert to restassured or drop
//		run {
//			val createRsp = restTemplate.postForEntity<String?>(baseUrl, null, String::class.java)
//			assertEquals(201, createRsp.statusCode.value(), createRsp.toString())
//		}
//		run {
//			val findRsp1 = findCkeckouts(nr)
//			assertEquals(200, findRsp1.statusCode.value(), findRsp1.toString())
//			assertEquals(1, findRsp1.body!!.size, findRsp1.toString())
//		}
//		run {
//			restTemplate.patchForObject("${baseUrl}/return", null, String::class.java)
//
//			val findRsp2 = findCkeckouts(nr)
//			assertEquals(200, findRsp2.statusCode.value(), findRsp2.toString())
//			assertEquals(0, findRsp2.body!!.size, findRsp2.toString())
//		}
	}

	@Test
	fun createFindReturn_withRestAssured() {
		//qqqq
	}
}
