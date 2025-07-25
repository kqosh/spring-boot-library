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
import java.time.ZonedDateTime

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CheckoutControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	lateinit var adminJwt: String
	lateinit var userJwt: String
	lateinit var user2Jwt: String

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
		adminJwt = UserControllerTest.createJwt(port!!, "admin", "pwadmin", 200)
		userJwt =  UserControllerTest.createJwt(port!!, "user101", "pwuser", 200)
		user2Jwt = UserControllerTest.createJwt(port!!, "user102", "pwuser", 200)
	}

	private fun findByUsername(username: String, expectedStatusCode: Int, jwt: String): List<CheckoutDto> {
		return getByUsername(username, expectedStatusCode, jwt).`as`(object : TypeRef<List<CheckoutDto>>() {})
	}

	private fun getByUsername(username: String, expectedStatusCode: Int, jwt: String): ResponseBodyExtractionOptions =
		get("http://localhost:${port}/checkouts/${username}", expectedStatusCode, jwt)

	private fun findByBook(isbn: String, expectedStatusCode: Int, jwt: String): List<CheckoutDto> {
		return getByBook(isbn, expectedStatusCode, jwt).`as`(object : TypeRef<List<CheckoutDto>>() {})
	}

	private fun getByBook(isbn: String, expectedStatusCode: Int, jwt: String): ResponseBodyExtractionOptions =
		get("http://localhost:${port}/checkouts/book/${isbn}", expectedStatusCode, jwt)

	private fun get(url: String, expectedStatusCode: Int, jwt: String): ResponseBodyExtractionOptions {
		return RestCallBuilder(url, expectedStatusCode).jwt(jwt).get()
	}

	@Test
	fun findByUsername() {
		val username = "user101"

		val checkouts: List<CheckoutDto> = findByUsername(username, 200, userJwt)

		assertEquals(2, checkouts.size)

		val checkoutsByIsbn: Map<String, CheckoutDto> = checkouts.associateBy { it.book.isbn }
		assertEquals(LocalDate.of(2025, 7, 8), checkoutsByIsbn["isbn123"]!!.checkoutAt.toLocalDate())
		assertEquals(LocalDate.of(2025, 7, 29), checkoutsByIsbn["isbn123"]!!.dueDate.toLocalDate())
		assertEquals(LocalDate.of(2025, 7, 15), checkoutsByIsbn["isbn124"]!!.checkoutAt.toLocalDate())
		assertEquals(LocalDate.of(2025, 8, 5), checkoutsByIsbn["isbn124"]!!.dueDate.toLocalDate())
	}

	@Test
	fun findByUsername_noAccess() {
		val username = "DoesntExist"
		getByUsername(username, 403, userJwt)
	}

	@Test
	fun findByUsername_forOtherUserNotAllowed() {
		val username = "user101"
		assertTrue(
			getByUsername(username, 403, user2Jwt).asString().contains("other user not allowed")
		)
	}

	@Test
	fun findByUsername_notFound() {
		val username = "Doesnt Exist"
		assertTrue(getByUsername(username, 400, adminJwt).asString().contains("user not found: $username"))
	}

	@Test
	fun findByUsername_asAdmin() {
		val username = "user101"
		val checkouts = findByUsername(username, 200, adminJwt)
		assertEquals(2, checkouts.size)
		val expectedIsbns = listOf("isbn123", "isbn124")
		checkouts.forEach { assertTrue(it.book.isbn in expectedIsbns) }
	}

	@Test
	fun findByBook_asUserNotAllowed() {
		getByBook("isbn123", 403, userJwt)
	}

	@Test
	fun findByBook_asAdminFound() {
		val checkouts = findByBook("isbn123", 200, adminJwt)
		assertEquals(1, checkouts.size)
		assertEquals("isbn123", checkouts[0].book.isbn)
		assertEquals("user101", checkouts[0].user.username)
	}

	@Test
	fun findByBook_asAdminNotFound() {
		val isbn = "isbn777"
		assertTrue(
			getByBook(isbn, 400, adminJwt).asString().contains("book not found: $isbn")
		)
	}

	@Test
	fun createFindRenewReturn_asUser() {
		createFindRenewReturn(user2Jwt)
	}

	@Test
	fun createFindRenewReturn_asAdminForUser() {
		createFindRenewReturn(adminJwt)
	}

	private fun createFindRenewReturn(jwt: String) {
		val username = "user102"
		val isbn = "isbn444"
		val baseUrl = createBaseUrl(username, isbn)
		var originalDueDate: ZonedDateTime? = null
		run {
			builder(baseUrl, 201, jwt).post()

			val checkouts = findByUsername(username, 200, jwt)
			assertEquals(1, checkouts.size, checkouts.toString())
			assertEquals(0, checkouts[0].renewCount, checkouts[0].toString())
			originalDueDate = checkouts[0].dueDate

			val rsp = builder(baseUrl, 400, jwt).post().asString()
			assertTrue(rsp.contains("max one copy can be borrowed: $username $isbn"))
		}
		run {
			builder("${baseUrl}/renew", 200, jwt).patch()

			val checkouts = findByUsername(username, 200, jwt)
			assertEquals(1, checkouts.size, checkouts.toString())
			assertEquals(1, checkouts[0].renewCount, checkouts[0].toString())
			assertTrue(checkouts[0].dueDate.isAfter(originalDueDate), checkouts[0].toString())

			val rsp = builder("${baseUrl}/renew", 400, jwt).patch().asString()
			assertTrue(rsp.contains("max renew count (1) exceeded"))
		}
		run {
			builder("${baseUrl}/return", 200, jwt).patch()

			val checkouts = findByUsername(username, 200, jwt)
			assertEquals(0, checkouts.size, checkouts.toString())
		}
	}

	private fun builder(url: String, expectedStatusCode: Int, jwt: String): RestCallBuilder =
		RestCallBuilder(url, expectedStatusCode).jwt(jwt)

	private fun createBaseUrl(username: String, isbn: String): String ="http://localhost:${port}/checkouts/${username}/${isbn}"

	@Test
	fun create_userNotFound() {
		val username = "user123"
		val isbn = "isbn101"
		val rsp = builder(createBaseUrl(username, isbn), 400, adminJwt).post().asString()
		assertTrue(rsp.contains("user not found: $username"))
	}

	@Test
	fun create_bookNotFound() {
		val username = "user102"
		val isbn = "isnotthere"
		val rsp = builder(createBaseUrl(username, isbn), 400, user2Jwt).post().asString()
		assertTrue(rsp.contains("book not found: $isbn"))
	}

	@Test
	fun create_bookNotAvailable() {
		val username = "user102"
		val isbn = "isbn125"
		val rsp = builder(createBaseUrl(username, isbn), 409, user2Jwt).post().asString()
		assertTrue(rsp.contains("currently no books available for: isbn125"))
	}

	@Test
	fun create_forOtherUserNotAllowed() {
		val username = "user101"
		val isbn = "isbn123"
		val rsp = builder(createBaseUrl(username, isbn), 403, user2Jwt).post().asString()
		assertTrue(rsp.contains("other user not allowed"))
	}

	@Test
	fun return_notFound() {
		val username = "user102"
		val isbn = "isbn777"
		val rsp = builder("${createBaseUrl(username, isbn)}/return", 404, user2Jwt).patch().asString()
		assertTrue(rsp.contains("/checkouts/${username}/${isbn}"))
	}

	@Test
	fun return_forOtherUserNotAllowed() {
		val username = "user101"
		val isbn = "isbn123"
		val rsp = builder("${createBaseUrl(username, isbn)}/return", 403, user2Jwt).patch().asString()
		assertTrue(rsp.contains("other user not allowed"))
	}

	@Test
	fun renew_notFound() {
		val username = "user102"
		val isbn = "isbn777"
		val rsp = builder("${createBaseUrl(username, isbn)}/renew", 404, user2Jwt).patch().asString()
		assertTrue(rsp.contains("/checkouts/${username}/${isbn}"))
	}

	@Test
	fun renew_forOtherUserNotAllowed() {
		val username = "user101"
		val isbn = "isbn123"
		val rsp = builder("${createBaseUrl(username, isbn)}/renew", 403, user2Jwt).patch().asString()
		assertTrue(rsp.contains("other user not allowed"))
	}

	@Test
	fun checkoutBook_overdue() {
		//qqqq
	}

	@Test
	fun renewBook_overdue() {
		//qqqq
	}


	@Test
	fun returnBook_overdue() {
		//qqqq
	}

	@Test
	fun checkoutBook_outstandingBalance() {
		//qqqq
	}

	@Test
	fun renewBook_outstandingBalance() {
		//qqqq
	}

	@Test
	fun returnBook_outstandingBalance() {
		//qqqq
	}
}
