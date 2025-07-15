package com.jdriven.library.presentation

import com.jdriven.library.service.model.UserDto
import io.restassured.RestAssured
import io.restassured.response.ResponseBodyExtractionOptions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest() {

	@LocalServerPort
	private var port: Int? = null

	lateinit var baseUrl: String

	@BeforeEach
	fun setup() {
		RestAssured.port = port!!
		baseUrl = "http://localhost:${port}/users"
	}

	private fun findByUsername(username: String, expectedStatusCode: Int, userId: String = "admin", password: String = "pwadmin"): ResponseBodyExtractionOptions {
		try {
			return RestCallBuilder("${baseUrl}/${username}", expectedStatusCode).username(userId).password(password).get()
		} catch(ex: Exception) {
			ex.printStackTrace()
			throw ex
		}
	}

	private fun findByUsernameAsUserDto(username: String, expectedStatusCode: Int, userId: String = "admin", password: String = "pwadmin"): UserDto {
		return findByUsername(username, expectedStatusCode, userId, password).`as`(UserDto::class.java)!!
	}

	@Test
	fun findByName_found() {
		val username = "user101"
		val user = findByUsernameAsUserDto(username, 200)

		assertEquals(username, user.username)
		assertEquals(1, user.roles.size)
		assertEquals("ROLE_USER", user.roles[0])
	}

	@Test
	fun findByName_loginUsernameDoesNotExist() {
		findByUsername("user101", 401, "user901", "pwuser").asString()
	}

	@Test
	fun findByName_wrongPassword() {
		findByUsername("user101", 401, "user101", "wrong-pw").asString()
	}

	@Test
	fun findByName_notFound() {
		val username = "Doesnt Exist"
		val rsp = findByUsername(username, 404).asString()
		assertTrue(rsp.contains(username.replace(" ", "%20")), rsp.toString())
	}

	@Test
	fun createFindDisable() {
		val username = "user801"
		val user = UserDto(username,"pw801", true, 30, listOf("ROLE_USER"))

		// when create
		RestCallBuilder(baseUrl, 201).body(user).username("admin").password("pwadmin").post()

		// then
		val rspUser = findByUsernameAsUserDto(username, 200)
		assertEquals(1, rspUser.roles.size)
		assertEquals(user.roles[0], rspUser.roles[0])
		assertEquals(user.enabled, rspUser.loanPeriodInDays)
		assertEquals(user.loanPeriodInDays, rspUser.loanPeriodInDays)

		findByUsername(username, 200, "user801", "pw801")

		// and disable
		RestCallBuilder("${baseUrl}/${username}?enabled=false", 200).username("admin").password("pwadmin").patch()

		// then
		findByUsername(username, 200)
		findByUsername(username, 404, "user801", "pw801")
	}

	@Test
	fun createWithInvalidRoleNotAllowed() {
		val username = "user701"
		val user = UserDto(username,"pw701", true, 30, listOf("ROLE_NON_EXISTEND"))

		// when create
		val rsp = RestCallBuilder(baseUrl, 400).body(user).username("admin").password("pwadmin").post().asString()
		assertTrue(rsp.contains("qqqq"))
	}
}
