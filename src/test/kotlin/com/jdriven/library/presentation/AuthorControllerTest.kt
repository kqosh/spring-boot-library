package com.jdriven.library.presentation

import com.jdriven.library.service.model.AuthorDto
import com.jdriven.library.service.model.CreateOrUpdateAuthorRequest
import com.jdriven.library.service.model.PaginatedResponse
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthorControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	private fun findByName(name: String, expectedStatusCode: Int, userId: String = "admin", password: String = "pwadmin"): ResponseBodyExtractionOptions {
		return RestCallBuilder("http://localhost:${port}/authors/${name}", expectedStatusCode).username(userId).password(password).get()
	}

	@Test
	fun findByName_found() {
		val name = "Jan Klaassen"
		val author = findByName(name, 200).`as`(AuthorDto::class.java)!!

		assertEquals(name, author.name)
		assertEquals(2, author.books.size)
		val isbns = author.books.map { b -> b.isbn }
		assertTrue(isbns.contains("isbn123"))
		assertTrue(isbns.contains("isbn124"))
	}

	@Test
	fun findByName_loginUsernameDoesNotExist() {
		val name = "Jan Klaassen"
		findByName(name, 401, "nr013", "pwuser").asString()
	}

	@Test
	fun findByName_wrongPassword() {
		val name = "Jan Klaassen"
		findByName(name, 401, "user101", "wrong-pw").asString()
	}

	@Test
	fun findByName_notFound() {
		val name = "DoesntExist"
		val rsp = findByName(name, 404).asString()
		assertTrue(rsp.contains(name), rsp.toString())
	}

	@Test
	fun createFindDelete() {
		val baseUrl = "http://localhost:${port}/authors"
		val name = "Klaas"
		RestCallBuilder(baseUrl, 201)
			.body(CreateOrUpdateAuthorRequest(name))
			.username("admin")
			.password("pwadmin")
			.post()

		findByName(name, 200)

		RestCallBuilder("${baseUrl}/${name}", 200).username("admin").password("pwadmin").delete()
		findByName(name, 404)
	}

	@Test
	fun create_notAllowed() {
		RestCallBuilder("http://localhost:${port}/authors", 403)
			.body(CreateOrUpdateAuthorRequest("Henk"))
			.username("user101")
			.password("pwuser")
			.post()
	}

	@Test
	fun delete_notAllowedByUser() {
		assertTrue(
			RestCallBuilder("http://localhost:${port}/authors/Henk", 403).username("user101").password("pwuser")
				.delete().asString().contains("/authors/Henk")
		)
	}

	@Test
	fun delete_notAllowedBecauseThereIsStillABook() {
		assertTrue(
			RestCallBuilder("http://localhost:${port}/authors/Jan Klaassen", 409).username("admin").password("pwadmin")
				.delete().asString().contains("this author still has books")
		)
	}

	@Test
	fun search_byAuthorNotFound() {
		val page = searchAsAuthors("Q", 200)
		assertEquals(0, page.content.size)
	}

	@Test
	fun search_byName() {
		val page = searchAsAuthors("k", 200)
		assertEquals(3, page.content.size)
		page.content.forEach { assertTrue(it.name.startsWith("K")) }
	}

	@Test
	fun search_byNamePage1Size2() {
		val page = searchAsAuthors("k", 200, pageIndex = 1, pageSize = 2)
		assertEquals(1, page.content.size)
		page.content.forEach { assertTrue(it.name.startsWith("Klaas")) }
	}

	@Test
	fun search_byNameEmpty() {
		assertTrue(searchAsRspOptions("", 400).asString().contains("authorName must not be empty"))
	}

	@Test
	fun search_byNameNull() {
		assertTrue(searchAsRspOptions(null, 400).asString().contains("authorName must not be empty"))
	}

	private fun searchAsAuthors(author: String?, expectedStatusCode: Int, pageIndex: Int = 0, pageSize: Int? = null): PaginatedResponse<AuthorDto> =
		searchAsRspOptions(author, expectedStatusCode, pageIndex, pageSize).`as`(object : TypeRef<PaginatedResponse<AuthorDto>>() {})

	private fun searchAsRspOptions(name: String?, expectedStatusCode: Int, pageIndex: Int = 0, pageSize: Int? = null): ResponseBodyExtractionOptions {
		var url = "http://localhost:${port}/authors/search?page=${pageIndex}"
		if (pageSize != null) url += "&size=${pageSize}"
		if (name != null) url += "&name=${name}"
		return RestCallBuilder(url, expectedStatusCode).username("user101").password("pwuser").get()
	}
}
