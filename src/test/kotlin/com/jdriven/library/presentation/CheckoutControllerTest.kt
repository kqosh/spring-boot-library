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

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	@Test
	fun findByUsername() {
		val username = "user101"

		val checkouts: List<CheckoutDto> = findByUsername(username, 200)

		assertEquals(2, checkouts.size)

		val checkoutsByIsbn: Map<String, CheckoutDto> = checkouts.associateBy { it.book.isbn }
		assertEquals(LocalDate.of(2025, 7, 8), checkoutsByIsbn["isbn123"]!!.checkoutAt.toLocalDate())
		assertEquals(LocalDate.of(2025, 7, 29), checkoutsByIsbn["isbn123"]!!.dueDate.toLocalDate())
		assertEquals(LocalDate.of(2025, 7, 15), checkoutsByIsbn["isbn124"]!!.checkoutAt.toLocalDate())
		assertEquals(LocalDate.of(2025, 8, 5), checkoutsByIsbn["isbn124"]!!.dueDate.toLocalDate())
	}

	private fun findByUsername(username: String, expectedStatusCode: Int, loginUsername: String = username, password: String = "pwuser"): List<CheckoutDto> {
		return getByUsername(username, expectedStatusCode, loginUsername, password).`as`(object : TypeRef<List<CheckoutDto>>() {})
	}

	private fun getByUsername(username: String, expectedStatusCode: Int, loginUsername: String, password: String): ResponseBodyExtractionOptions =
		get("http://localhost:${port}/checkouts/${username}", expectedStatusCode, loginUsername, password)

	private fun findByBook(isbn: String, expectedStatusCode: Int, loginUsername: String, password: String = "pwuser"): List<CheckoutDto> {
		return getByBook(isbn, expectedStatusCode, loginUsername, password).`as`(object : TypeRef<List<CheckoutDto>>() {})
	}

	private fun getByBook(isbn: String, expectedStatusCode: Int, loginUsername: String, password: String): ResponseBodyExtractionOptions =
		get("http://localhost:${port}/checkouts/book/${isbn}", expectedStatusCode, loginUsername, password)

	private fun get(url: String, expectedStatusCode: Int, loginUsername: String, password: String): ResponseBodyExtractionOptions {
		return RestCallBuilder(url, expectedStatusCode)
			.username(loginUsername)
			.password(password)
			.get()
	}

	@Test
	fun findByUsername_noAccess() {
		val username = "DoesntExist"
		getByUsername(username, 401, username, "pwuser")
	}

	@Test
	fun findByUsername_forOtherUserNotAllowed() {
		val username = "user101"
		assertTrue(
			getByUsername(username, 403, "user102", "pwuser").asString().contains("other user not allowed")
		)
	}

	@Test
	fun findByUsername_notFound() {
		val username = "Doesnt Exist"
		assertTrue(getByUsername(username, 400, "admin", "pwadmin").asString().contains("user not found: $username"))
	}

	@Test
	fun findByUsernameAsAdmin() {
		val username = "user101"
		val checkouts = findByUsername(username, 200, "admin", "pwadmin")
		assertEquals(2, checkouts.size)
		val expectedIsbns = listOf("isbn123", "isbn124")
		checkouts.forEach { assertTrue(it.book.isbn in expectedIsbns) }
	}

	@Test
	fun findByBook_byUserNotAllowed() {
		assertTrue(
			getByBook("isbn123", 403, "user101", "pwuser").asString().contains("/checkouts/book/isbn123")
		)
	}

	@Test
	fun findByBook_asAdminFound() {
		val checkouts = findByBook("isbn123", 200, "admin", "pwadmin")
		assertEquals(1, checkouts.size)
		assertEquals("isbn123", checkouts[0].book.isbn)
		assertEquals("user101", checkouts[0].user.username)
	}

	@Test
	fun findByBook_asAdminNotFound() {
		val isbn = "isbn777"
		assertTrue(
			getByBook(isbn, 400, "admin", "pwadmin").asString().contains("book not found: $isbn")
		)
	}

	@Test
	fun createFindRenewReturnByUser() {
		createFindRenewReturn("user102", "pwuser")
	}

	@Test
	fun createFindRenewReturnAsAdminForUser() {
		createFindRenewReturn("admin", "pwadmin")
	}

	private fun createFindRenewReturn(loginUsername: String, password: String) {
		val username = "user102"
		val isbn = "isbn444"
		val baseUrl = createBaseUrl(username, isbn)
		var originalDueDate: ZonedDateTime? = null
		run {
			builder(baseUrl, 201, loginUsername, password).post()

			val checkouts = findByUsername(username, 200)
			assertEquals(1, checkouts.size, checkouts.toString())
			assertEquals(0, checkouts[0].renewCount, checkouts[0].toString())
			originalDueDate = checkouts[0].dueDate

			val rsp = builder(baseUrl, 400, loginUsername, password).post().asString()
			assertTrue(rsp.contains("max one copy can be borrowed: $username $isbn"))
		}
		run {
			builder("${baseUrl}/renew", 200, loginUsername, password).patch()

			val checkouts = findByUsername(username, 200)
			assertEquals(1, checkouts.size, checkouts.toString())
			assertEquals(1, checkouts[0].renewCount, checkouts[0].toString())
			assertTrue(checkouts[0].dueDate.isAfter(originalDueDate), checkouts[0].toString())

			val rsp = builder("${baseUrl}/renew", 400, loginUsername, password).patch().asString()
			assertTrue(rsp.contains("max renew count (1) exceeded"))
		}
		run {
			builder("${baseUrl}/return", 200, loginUsername, password).patch()

			val checkouts = findByUsername(username, 200)
			assertEquals(0, checkouts.size, checkouts.toString())
		}
	}

	private fun builder(url: String, expectedStatusCode: Int, loginUserName: String = "user102", password: String = "pwuser"): RestCallBuilder =
		RestCallBuilder(url, expectedStatusCode).username(loginUserName).password(password)

	private fun createBaseUrl(username: String, isbn: String): String ="http://localhost:${port}/checkouts/${username}/${isbn}"

	@Test
	fun create_userNotFound() {
		val username = "user123"
		val isbn = "isbn101"
		val rsp = builder(createBaseUrl(username, isbn), 400, "admin", "pwadmin").post().asString()
		assertTrue(rsp.contains("user not found: $username"))
	}

	@Test
	fun create_bookNotFound() {
		val username = "user102"
		val isbn = "isnotthere"
		val rsp = builder(createBaseUrl(username, isbn), 400).post().asString()
		assertTrue(rsp.contains("book not found: $isbn"))
	}

	@Test
	fun create_bookNotAvailable() {
		val username = "user102"
		val isbn = "isbn125"
		val rsp = builder(createBaseUrl(username, isbn), 409).post().asString()
		assertTrue(rsp.contains("currently no books available for: isbn125"))
	}

	@Test
	fun create_forOtherUserNotAllowed() {
		val username = "user101"
		val isbn = "isbn123"
		val rsp = builder(createBaseUrl(username, isbn), 403).post().asString()
		assertTrue(rsp.contains("other user not allowed"))
	}
//
//	@Test
//	fun create_forOtherUserAsAdmin() {
//		val username = "user101"
//		val isbn = "isbn123"
//		val rsp = builder(createBaseUrl(username, isbn), 201, "admin", "pwadmin").post().asString()
//		findByUsername(username, 200).
//	}qqqq

	@Test
	fun return_notFound() {
		val username = "user102"
		val isbn = "isbn777"
		val rsp = builder("${createBaseUrl(username, isbn)}/return", 404).patch().asString()
		assertTrue(rsp.contains("/checkouts/${username}/${isbn}"))
	}

	@Test
	fun return_forOtherUserNotAllowed() {//qqqq mag wel by admin
		val username = "user101"
		val isbn = "isbn123"
		val rsp = builder("${createBaseUrl(username, isbn)}/return", 403).patch().asString()
		assertTrue(rsp.contains("other user not allowed"))
	}

	@Test
	fun renew_notFound() {
		val username = "user102"
		val isbn = "isbn777"
		val rsp = builder("${createBaseUrl(username, isbn)}/renew", 404).patch().asString()
		assertTrue(rsp.contains("/checkouts/${username}/${isbn}"))
	}

	@Test
	fun renew_forOtherUserNotAllowed() {//qqqq mag wel by admin
		val username = "user101"
		val isbn = "isbn123"
		val rsp = builder("${createBaseUrl(username, isbn)}/renew", 403).patch().asString()
		assertTrue(rsp.contains("other user not allowed"))
	}
}
