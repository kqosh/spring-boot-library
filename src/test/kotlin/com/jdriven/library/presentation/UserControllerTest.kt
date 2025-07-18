package com.jdriven.library.presentation

import com.jdriven.library.service.model.CreateUserRequest
import com.jdriven.library.service.model.UserDto
import io.restassured.RestAssured
import io.restassured.response.ResponseBodyExtractionOptions
import org.junit.jupiter.api.Assertions.*
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
		val username = "DoesntExist"
		val rsp = findByUsername(username, 404).asString()
		assertTrue(rsp.contains(username), rsp.toString())
	}

	@Test
	fun createFindAddRoleDisable() {
		val username = "user801"
		val pw = "pw801"
		val createRequest = CreateUserRequest(username,pw, true, 30)
		run {
			// when create
			RestCallBuilder(baseUrl, 201).body(createRequest).username("admin").password("pwadmin").post()

			// then
			val user = findByUsernameAsUserDto(username, 200)
			assertEquals(0, user.roles.size)
			assertEquals(createRequest.enabled, user.enabled)
			assertEquals(createRequest.loanPeriodInDays, user.loanPeriodInDays)

			findByUsername(username, 401, username, pw) // Because this user has no roles yet!
		}
		run {
			// and add role
			val role = "ROLE_USER"
			roleCallBuilder(username, role, 201).post()

			// then
			val user = findByUsernameAsUserDto(username, 200, username, pw)
			assertEquals(1, user.roles.size)
			assertEquals(role, user.roles[0])
		}
		run {
			// and disable
			RestCallBuilder("${baseUrl}/${username}?enabled=false", 200).username("admin").password("pwadmin").patch()

			// then
			findByUsername(username, 401, username, pw)
			val user = findByUsernameAsUserDto(username, 200)
			assertFalse(user.enabled)
		}
	}

	private fun roleCallBuilder(username: String, role: String, expectedStatusCode: Int): RestCallBuilder =
		RestCallBuilder("${baseUrl}/${username}/roles/${role}", expectedStatusCode)
			.username("admin")
			.password("pwadmin")

	@Test
	fun addRole_duplicateRole() {
		val username = "user101"
		val role = "ROLE_USER"
		assertTrue(
			roleCallBuilder(username, role, 409).post().asString()
				.contains("authority already exists for $username $role")
		)
	}

	@Test
	fun addRole_toNonExistingUser() {
		val username = "userNonExisting"
		val role = "ROLE_USER"
		assertTrue(
			roleCallBuilder(username, role, 400).post().asString()
				.contains("user does not exist $username")
		)
	}

	@Test
	fun addRole_nonExistingRole() {
		val username = "user101"
		val role = "ROLE_DOES_NOT_EXIST"
		assertTrue(
			roleCallBuilder(username, role, 400).post().asString()
				.contains("non existing role $role")
		)
	}

	@Test
	fun deleteRole_fromNonExistingUser() {
		val username = "userNonExisting"
		val role = "ROLE_USER"
		assertTrue(
			roleCallBuilder(username, role, 404).delete().asString()
				.contains("/users/$username/roles/$role")
		)
	}

	@Test
	fun deleteRole_nonExistingRole() {
		val username = "user101"
		val role = "ROLE_ADMIN"
		assertTrue(
			roleCallBuilder(username, role, 404).delete().asString()
				.contains("/users/$username/roles/$role")
		)
	}
}
