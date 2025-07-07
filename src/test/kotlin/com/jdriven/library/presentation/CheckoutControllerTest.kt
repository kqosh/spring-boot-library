package com.jdriven.library.presentation

import com.jdriven.library.service.model.Checkout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import java.time.LocalDate

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CheckoutControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Test
	fun findByMemberNumber() {
		val nr = "nr101"

		val responseEntity = findCkeckouts(nr)

		val checkouts: List<Checkout> = responseEntity.body!!
		assertEquals(200, responseEntity.statusCode.value(), responseEntity.toString())
		assertEquals(2, checkouts.size, responseEntity.toString())

		val checkoutsByIsbn: Map<String, Checkout> = checkouts.associateBy { it.book.isbn }
		assertEquals(LocalDate.of(2025, 7, 8), checkoutsByIsbn["isbn123"]!!.checkoutAt, responseEntity.toString())
		assertEquals(LocalDate.of(2025, 7, 15), checkoutsByIsbn["isbn124"]!!.checkoutAt, responseEntity.toString())
	}

	private fun findCkeckouts(memberNr: String): ResponseEntity<List<Checkout>> {
		val responseType = object : ParameterizedTypeReference<List<Checkout>>() {}
		return restTemplate.exchange("http://localhost:${port}/checkouts/${memberNr}", HttpMethod.GET, null, responseType)
	}

	@Test
	fun findByMemberNumber_notFound() {
		val nr = "Doesnt Exist"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/checkouts/${nr}", String::class.java)
		assertEquals(404, rsp.statusCode.value(), rsp.toString())
		assertTrue(rsp.body!!.contains(nr.replace(" ", "%20")), rsp.toString())
	}

	@Test
	fun createFindReturn() {
		val nr = "nr102"
		val isbn = "isbn444"
		val baseUrl = "http://localhost:${port}/checkouts/${nr}/${isbn}"
		run {
			val createRsp = restTemplate.postForEntity<String?>(baseUrl, null, String::class.java)
			assertEquals(201, createRsp.statusCode.value(), createRsp.toString())
		}
		run {
			val findRsp1 = findCkeckouts(nr)
			assertEquals(200, findRsp1.statusCode.value(), findRsp1.toString())
			assertEquals(1, findRsp1.body!!.size, findRsp1.toString())
		}
		run {
			restTemplate.patchForObject("${baseUrl}/return", null, String::class.java)

			val findRsp2 = findCkeckouts(nr)
			assertEquals(200, findRsp2.statusCode.value(), findRsp2.toString())
			assertEquals(0, findRsp2.body!!.size, findRsp2.toString())
		}
	}

	@Test
	fun createFindReturn_withRestAssured() {
		//qqqq
	}
}
