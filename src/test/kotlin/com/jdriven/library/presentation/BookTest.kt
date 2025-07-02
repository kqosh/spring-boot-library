package com.jdriven.library.presentation;

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.crossstore.ChangeSetPersister

//import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BookTest() {

	@LocalServerPort
	private var port: Int? = null

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Test
	fun found() {
		Assertions.assertThat(get("qqqq123").contains("qqqq"));
	}

	@Test
	fun notFound() {
		try {
			get("qqqq123").contains("qqqq404")
		} catch (ex: Exception) {
			Assertions.assertThat(ex is ChangeSetPersister.NotFoundException)
		}
	}

	private fun get(isbn: String): String {//qqqq Book
		return restTemplate.getForObject("http://localhost:${port}/books/qqqqq", String::class.java);
	}
}
