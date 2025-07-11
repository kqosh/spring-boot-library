package com.jdriven.library.presentation

import io.restassured.RestAssured
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class VersionTest() {

	@LocalServerPort
	private var port: Int? = null

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
	}

	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Test
	fun version() {
		val body = RestCallBuilder("http://localhost:${port}/version", 200).get().asString()
		Assertions.assertTrue(body.contains("0.0.1-SNAPSHOT"))
	}
}
