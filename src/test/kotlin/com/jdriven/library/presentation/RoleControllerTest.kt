package com.jdriven.library.presentation

import io.restassured.RestAssured
import io.restassured.common.mapper.TypeRef
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RoleControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	lateinit var adminJwt: String

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
		adminJwt = UserControllerTest.createJwt(port!!, "admin", "pwadmin", 200)
	}

	@Test
	fun findAllRoles() {
		val roles = RestCallBuilder("http://localhost:${port}/roles", 200).jwt(adminJwt).get().`as`(object : TypeRef<List<String>>() {})
		assertEquals(2, roles.size)
		assertTrue(roles.contains("ROLE_ADMIN"))
		assertTrue(roles.contains("ROLE_USER"))
	}
}
