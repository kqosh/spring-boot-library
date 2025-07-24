package com.jdriven.library.presentation

import com.jdriven.library.service.model.BookDto
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
class BookControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	lateinit var adminJwt: String
	lateinit var userJwt: String

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
		adminJwt = UserControllerTest.createJwt(port!!, "admin", "pwadmin", 200)
		userJwt = UserControllerTest.createJwt(port!!, "user101", "pwuser", 200)
	}

	private fun findByIsbn(isbn: String, expectedStatusCode: Int): ResponseBodyExtractionOptions =
		RestCallBuilder("http://localhost:${port}/books/${isbn}", expectedStatusCode).jwt(userJwt).get()

	@Test
	fun findIsbn_found() {
		val isbn = "isbn123"
		val book = findByIsbn(isbn, 200).`as`(BookDto::class.java)!!
		assertEquals(isbn, book.isbn)
		assertEquals("Jan Klaassen", book.authorName)
	}

	@Test
	fun findIsbn_notFound() {
		val isbn = "isbnNotFound"
		val body = findByIsbn(isbn, 404).asString()
		assertTrue(body.contains(isbn), body)
	}

	@Test
	fun search_byAuthor() {
		search_byAuthor("RENE", true)
		search_byAuthor("goscinny", true, 0)
		search_byAuthor("RENE", false)
		search_byAuthor("goscinny", false)
	}

	private fun search_byAuthor(author: String, startsWith: Boolean, exptectedHits: Int = 1) {
		val page = searchAsBooks(author, "", startsWith, 200)
		assertEquals(exptectedHits, page.content.size)
		if (exptectedHits > 0) assertEquals("Rene Goscinny", page.content[0].authorName)
	}

	@Test
	fun search_byAuthorTooManyHits() {
		val rsp = searchAsRspOptions("kees", "", false, 400).asString()
		assertTrue(rsp.contains("too many hits"))
	}

	@Test
	fun search_byAuthorNotFound() {
		var page = searchAsBooks("HARRY", null, false, 200)
		assertEquals(0, page.content.size)

		page = searchAsBooks("HARRY", null, true, 200)
		assertEquals(0, page.content.size)
	}

	@Test
	fun search_noArgs() {
		search_noArgs(null, null)
		search_noArgs(null, "")
		search_noArgs("", null)
		search_noArgs("", "")
	}

	fun search_noArgs(author: String?, title: String?) {
		val rsp = searchAsRspOptions(author, title, false, 400).asString()
		assertTrue(rsp.contains("authorName and title must not be both empty"))
	}

	@Test
	fun search_byTitle() {
		search_byTitle(false, "poppenkast")
		search_byTitle(false, "de poppenkast")
		search_byTitle(true, "de poppenkast")
		search_byTitle(true, "poppenkast", 0)
	}

	private fun search_byTitle(startsWith: Boolean, titleTerm: String, expectedHits: Int = 3) {
		val page = searchAsBooks(null, titleTerm, startsWith, 200)
		assertEquals(expectedHits, page.content.size)
		if (expectedHits > 0) page.content.forEach { assertTrue(it.title!!.startsWith("De poppenkast")) }
	}

	@Test
	fun search_byTitlePage0Size2() {
		search_byTitlePage0Size2(false)
		search_byTitlePage0Size2(true)
	}

	private fun search_byTitlePage0Size2(startsWith: Boolean) {
		val page = searchAsBooks(null, "de poppenkast", startsWith, 200, 0, 2)
		assertEquals(2, page.content.size)
		page.content.forEach { assertTrue(it.authorName!!.startsWith("Jan")) }
	}

	@Test
	fun search_byTitlePage1Size2() {
		search_byTitlePage1Size2(false)
		search_byTitlePage1Size2(true)
	}

	fun search_byTitlePage1Size2(startsWith: Boolean) {
		val page = searchAsBooks(null, "de poppenkast", startsWith, 200, 1, 2)
		assertEquals(1, page.content.size)
		page.content.forEach { assertTrue(it.authorName!!.startsWith("Katrijn")) }
	}

	@Test
	fun search_byAuthorAndTitle() {
		search_byAuthorAndTitle(false)
		search_byAuthorAndTitle(true)
	}

	private fun search_byAuthorAndTitle(startsWith: Boolean) {
		val page = searchAsBooks("jan", "de poppenkast", startsWith, 200)
		assertEquals(2, page.content.size)
		page.content.forEach { assertTrue(it.title!!.startsWith("De poppenkast")) }
	}

	private fun searchAsBooks(author: String?, title: String?, startsWith: Boolean, expectedStatusCode: Int, pageIndex: Int = 0, pageSize: Int? = null): PaginatedResponse<BookDto> =
		searchAsRspOptions(author, title, startsWith, expectedStatusCode, pageIndex, pageSize).`as`(object : TypeRef<PaginatedResponse<BookDto>>() {})

	private fun searchAsRspOptions(author: String?, title: String?, startsWith: Boolean, expectedStatusCode: Int, pageIndex: Int = 0, pageSize: Int? = null): ResponseBodyExtractionOptions {
		var url = "http://localhost:${port}/books/search${if (startsWith) "-starts-with" else ""}?page=${pageIndex}"
		if (pageSize != null) url += "&size=${pageSize}"
		if (!author.isNullOrBlank()) url += "&author=${author}"
		if (!title.isNullOrBlank()) url += "&title=${title}"
		return RestCallBuilder(url, expectedStatusCode).jwt(userJwt).get()
	}

	@Test
	fun createFindUpdateDelete() {
		val baseUrl = "http://localhost:${port}/books"
		val isbn = "isbn998"
		val isbnNew = "isbn999"
		val book = BookDto(isbn, "an author", "a title", numberOfCopies = 4)

		// create book
		RestCallBuilder(baseUrl, 201).body(book).jwt(adminJwt).post()
		findByIsbn(isbn, 200)
		// find newly added author
		RestCallBuilder("http://localhost:${port}/authors/${book.authorName}", 200).body(book).jwt(adminJwt).get()

		// update author
		RestCallBuilder("${baseUrl}/${isbn}", 200).body(book.copy(authorName = "Rene Goscinny")).jwt(adminJwt).put()
		val bookAfterUpdate = findByIsbn(isbn, 200).`as`(BookDto::class.java)!!
		assertEquals("Rene Goscinny", bookAfterUpdate.authorName)

		// update isbn
		RestCallBuilder("${baseUrl}/${isbn}", 200).body(book.copy(isbn = isbnNew)).jwt(adminJwt).put()
		findByIsbn(isbn, 404)
		findByIsbn(isbnNew, 200)

		// update non existing book
		val isbnNonExisting = "DoesNotExist"
		RestCallBuilder("${baseUrl}/${isbnNonExisting}", 404).body(book.copy(isbnNonExisting)).jwt(adminJwt).put()

		RestCallBuilder("${baseUrl}/${isbnNew}", 200).jwt(adminJwt).delete()
		findByIsbn(isbn, 404)
		RestCallBuilder("${baseUrl}/${isbnNew}", 404).jwt(adminJwt).delete()
	}
}
