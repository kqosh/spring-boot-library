package com.jdriven.library.presentation

import com.jdriven.library.service.model.Book
import com.jdriven.library.service.model.PaginatedResponse
import io.restassured.RestAssured
import io.restassured.common.mapper.TypeRef
import io.restassured.response.ResponseBodyExtractionOptions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals

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
		val book = findByIsbn(isbn, 200).`as`(Book::class.java)!!
		Assertions.assertEquals(isbn, book.isbn)
		Assertions.assertEquals("Jan Klaassen", book.authorName)
	}

	@Test
	fun findIsbn_notFound() {
		val isbn = "isbnNotFound"
		val body = findByIsbn(isbn, 404).asString()
		Assertions.assertTrue(body.contains(isbn), body)
	}

	@Test
	fun search_byAuthor() {
		val booksPage = searchAsBooks("RENE", null, 200)
		Assertions.assertEquals(1, booksPage.content.size)
		Assertions.assertEquals("Rene Goscinny", booksPage.content[0].authorName)
	}

	@Test
	fun search_byAuthorNotFound() {
		val booksPage = searchAsBooks("HARRY", null, 200)
		Assertions.assertEquals(0, booksPage.content.size)
	}

	@Test
	fun search_NoArgs() {
		searchAsRspOptions("", null, 400)
	}

	@Test
	fun search_byTitle() {
		val booksPage = searchAsBooks(null, "DE POP", 200)
		Assertions.assertEquals(3, booksPage.content.size)
		booksPage.content.forEach { Assertions.assertTrue(it.title!!.startsWith("De poppenkast")) }
	}

	@Test
	fun search_byAuthorAndTitle() {
		val booksPage = searchAsBooks("jan", "de poppenkast", 200)
		Assertions.assertEquals(2, booksPage.content.size)
		booksPage.content.forEach { Assertions.assertTrue(it.title!!.startsWith("De poppenkast")) }
	}

	//qqqq pagesize=2

	private fun searchAsBooks(author: String?, title: String?, expectedStatusCode: Int): PaginatedResponse<Book> =
		searchAsRspOptions(author, title, expectedStatusCode).`as`(object : TypeRef<PaginatedResponse<Book>>() {})

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
		val book = Book(isbn, "an author", "a title", numberOfCopies = 4)
		run {
			RestCallBuilder(baseUrl, 201).body(book).username("admin").password("pwadmin").post()
		}
		run {
			findByIsbn(isbn, 200)
			//qqqq find additional author
		}
		run {
			// update author
			RestCallBuilder("${baseUrl}/${isbn}", 200).body(book.copy(authorName = "Rene Goscinny")).username("admin").password("pwadmin").put()
			val bookAfterUpdate = findByIsbn(isbn, 200).`as`(Book::class.java)!!
			assertEquals("Rene Goscinny", bookAfterUpdate.authorName)
		}
		run {
			// update isbn
			RestCallBuilder("${baseUrl}/${isbn}", 200).body(book.copy(isbn = isbnNew)).username("admin").password("pwadmin").put()
		}
		run {
			// update non existing book
			val isbnNonExisting = "DoesNotExist"
			RestCallBuilder("${baseUrl}/${isbnNonExisting}", 404).body(book.copy(isbnNonExisting)).username("admin").password("pwadmin").put()
		}
		run {
			RestCallBuilder("${baseUrl}/${isbnNew}", 200).username("admin").password("pwadmin").delete()
			findByIsbn(isbn, 404)
			RestCallBuilder("${baseUrl}/${isbnNew}", 404).username("admin").password("pwadmin").delete()
		}
	}
}
