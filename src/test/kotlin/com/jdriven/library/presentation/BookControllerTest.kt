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

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	private fun findByIsbn(isbn: String, expectedStatusCode: Int): ResponseBodyExtractionOptions =
		RestCallBuilder("http://localhost:${port}/books/${isbn}", expectedStatusCode).username("user101").password("pwuser").get()

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
		val page = searchAsBooks("RENE", null, 200)
		assertEquals(1, page.content.size)
		assertEquals("Rene Goscinny", page.content[0].authorName)
	}

	@Test
	fun search_byAuthorNotFound() {
		val page = searchAsBooks("HARRY", null, 200)
		assertEquals(0, page.content.size)
	}

	@Test
	fun search_NoArgs() {
		searchAsRspOptions("", null, 400)
	}

	@Test
	fun search_byTitle() {
		val page = searchAsBooks(null, "DE POP", 200)
		assertEquals(3, page.content.size)
		page.content.forEach { assertTrue(it.title!!.startsWith("De poppenkast")) }
	}

	@Test
	fun search_byTitlePageSize2() {
		val page = searchAsBooks(null, "DE POP", 200, 0, 2)
		assertEquals(2, page.content.size)
		page.content.forEach { assertTrue(it.authorName!!.startsWith("Jan")) }
	}

	@Test
	fun search_byAuthorAndTitle() {
		val page = searchAsBooks("jan", "de poppenkast", 200)
		assertEquals(2, page.content.size)
		page.content.forEach { assertTrue(it.title!!.startsWith("De poppenkast")) }
	}

	private fun searchAsBooks(author: String?, title: String?, expectedStatusCode: Int, pageIndex: Int = 0, pageSize: Int? = null): PaginatedResponse<BookDto> =
		searchAsRspOptions(author, title, expectedStatusCode, pageIndex, pageSize).`as`(object : TypeRef<PaginatedResponse<BookDto>>() {})

	private fun searchAsRspOptions(author: String?, title: String?, expectedStatusCode: Int, pageIndex: Int = 0, pageSize: Int? = null): ResponseBodyExtractionOptions {
		var url = "http://localhost:${port}/books/search?page=${pageIndex}"
		if (pageSize != null) url += "&size=${pageSize}"
		if (author != null) url += "&author=${author}"
		if (title != null) url += "&title=${title}"
		return RestCallBuilder(url, expectedStatusCode).username("user101").password("pwuser").get()
	}

	@Test
	fun createFindUpdateDelete() {
		val baseUrl = "http://localhost:${port}/books"
		val isbn = "isbn998"
		val isbnNew = "isbn999"
		val book = BookDto(isbn, "an author", "a title", numberOfCopies = 4)

		// create book
		RestCallBuilder(baseUrl, 201).body(book).username("admin").password("pwadmin").post()
		findByIsbn(isbn, 200)
		// find newly added author
		RestCallBuilder("http://localhost:${port}/authors/${book.authorName}", 200).body(book).username("admin").password("pwadmin").get()

		// update author
		RestCallBuilder("${baseUrl}/${isbn}", 200).body(book.copy(authorName = "Rene Goscinny")).username("admin").password("pwadmin").put()
		val bookAfterUpdate = findByIsbn(isbn, 200).`as`(BookDto::class.java)!!
		assertEquals("Rene Goscinny", bookAfterUpdate.authorName)

		// update isbn
		RestCallBuilder("${baseUrl}/${isbn}", 200).body(book.copy(isbn = isbnNew)).username("admin").password("pwadmin").put()
		findByIsbn(isbn, 404)
		findByIsbn(isbnNew, 200)

		// update non existing book
		val isbnNonExisting = "DoesNotExist"
		RestCallBuilder("${baseUrl}/${isbnNonExisting}", 404).body(book.copy(isbnNonExisting)).username("admin").password("pwadmin").put()

		RestCallBuilder("${baseUrl}/${isbnNew}", 200).username("admin").password("pwadmin").delete()
		findByIsbn(isbn, 404)
		RestCallBuilder("${baseUrl}/${isbnNew}", 404).username("admin").password("pwadmin").delete()
	}
}
