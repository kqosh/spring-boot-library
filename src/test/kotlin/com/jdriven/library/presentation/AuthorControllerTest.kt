package com.jdriven.library.presentation

import com.jdriven.library.presentation.model.Author
import com.jdriven.library.presentation.model.CreateAuthorRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpStatusCodeException





@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthorControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Test
	fun findByName_found() {
		val name = "Jan Klaassen"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/authors/${name}", Author::class.java)
		Assertions.assertEquals(200, rsp.statusCode.value(), rsp.toString())
		val author = rsp.body!!
		Assertions.assertEquals(name, author.name, rsp.toString())
		Assertions.assertEquals(2, author.books.size, rsp.toString())
		val isbns = author.books.map { b -> b.isbn }
		Assertions.assertTrue(isbns.contains("isbn123"), rsp.toString())
		Assertions.assertTrue(isbns.contains("isbn124"), rsp.toString())
	}

	@Test
	fun findByName_notFound() {
		val name = "Doesnt Exist"
		val rsp = restTemplate.getForEntity("http://localhost:${port}/authors/${name}", String::class.java)
		Assertions.assertEquals(404, rsp.statusCode.value(), rsp.toString())
		Assertions.assertTrue(rsp.body!!.contains(name.replace(" ", "%20")), rsp.toString())
	}

	@Test
	fun create() {
		val request = CreateAuthorRequest( "Jan")

		// Gebruik postForEntity als het een POST-request is
		val responseEntity: ResponseEntity<String?> =
			restTemplate.postForEntity<String?>(
				"http://localhost:${port}/authors",
				request,
				String::class.java
			)
		println("rsp=$responseEntity")
		}

//
//	@Test
//	fun createFindDeleteFind() {
//		val request = CreateAuthorRequest( "Jan")
//
//
//		// VERVANG de regel op lijn 53 met dit blok:
//		try {
//			// Gebruik postForEntity als het een POST-request is
//			val responseEntity: ResponseEntity<String?> =
//				restTemplate.postForEntity<String?>("http://localhost:${port}/authors/qqqq", request, String::class.java)
//
//
//			// Of gebruik getForEntity als het een GET-request is
//			// ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
//			println("--- DIAGNOSE INFO ---")
//			println("Status Code: " + responseEntity.getStatusCode())
//			println("Response Body: " + responseEntity.getBody())
//			println("--- EINDE DIAGNOSE ---")
//		} catch (e: HttpStatusCodeException) {
//			println("--- DIAGNOSE INFO (FOUT) ---")
//			println("Status Code: " + e.getStatusCode())
//			println("Response Body (uit exception): " + e.getResponseBodyAsString())
//			println("--- EINDE DIAGNOSE ---")
//		}
////		// Oorspronkelijke code (veroorzaakt de fout)
////// Author author = restTemplate.getForObject(url, Author.class);
////
////// TIJDELIJKE DIAGNOSE-CODE
////		val responseEntity: ResponseEntity<String?> = restTemplate.getForEntity<String?>(url, String::class.java)
////		println("Status Code: " + responseEntity.getStatusCode())
////		println("Response Body: " + responseEntity.getBody())
//
//
//		val authorCreated = restTemplate.postForObject("http://localhost:${port}/authors", request, Author::class.java)
//		Assertions.assertEquals(request.name, authorCreated.name)
//
//		val authorFound = restTemplate.getForEntity("http://localhost:${port}/authors/${request.name}", Author::class.java).body!!
//		Assertions.assertEquals(request.name, authorFound.name)
//
//		restTemplate.delete("http://localhost:${port}/authors/${authorFound.name}")
//		val qqqq = restTemplate.getForEntity("http://localhost:${port}/authors/${authorFound.name}", Author::class.java).body!!
//		Assertions.assertNull(qqqq)
//	}
}
