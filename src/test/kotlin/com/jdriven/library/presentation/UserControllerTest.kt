package com.jdriven.library.presentation

import com.jdriven.library.service.model.CreateJwtRequest
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
    lateinit var adminJwt: String

    @BeforeEach
    fun setup() {
        RestAssured.port = port!!
        baseUrl = "http://localhost:${port}/users"
        adminJwt = createAdminJwt(port!!, "admin", "pwadmin", 200)
    }

    companion object {

        fun createAdminJwt(
            port: Int, username: String = "admin", password: String = "pwadmin", expectedStatusCode: Int = 200
        ): String = createJwt(port, username, password, expectedStatusCode)

        fun createJwt(port: Int, username: String, password: String, expectedStatusCode: Int = 200): String {
            val url = "http://localhost:${port}/users"

            return RestCallBuilder("${url}/jwts", expectedStatusCode).body(CreateJwtRequest(username, password)).post()
                .asString()
        }
    }

    private fun createJwt(username: String, password: String, expectedStatusCode: Int = 200): String =
        createJwt(port!!, username, password, expectedStatusCode)

    private fun findByUsername(username: String, expectedStatusCode: Int, jwt: String): ResponseBodyExtractionOptions {
        try {
            return RestCallBuilder("${baseUrl}/${username}", expectedStatusCode).jwt(jwt).get()
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    private fun findByUsernameAsUserDto(username: String, expectedStatusCode: Int, jwt: String): UserDto {
        return findByUsername(username, expectedStatusCode, jwt).`as`(UserDto::class.java)!!
    }

    @Test
    fun createJwt() {
        assertTrue(createAdminJwt(port!!).length > 0)
    }

    @Test
    fun createJwt_wrongPassword() {
        createAdminJwt(port!!, password = "wrong-pw", expectedStatusCode = 401)
    }

    @Test
    fun createJwt_userDoesNotExist() {
        createAdminJwt(port!!, "user701", "pwadmin", expectedStatusCode = 404)
    }

    @Test
    fun findByName_foundAsAdmin() {
        findByName_found("user101", adminJwt)
    }

    @Test
    fun findByName_foundAsUser() {
        val username = "user101"
        findByName_found(username, createJwt(username, "pwuser"))
    }

    private fun findByName_found(username: String, jwt: String) {
        val user = findByUsernameAsUserDto(username, 200, jwt)

        assertEquals(username, user.username)
        assertEquals(1, user.roles.size)
        assertEquals("ROLE_USER", user.roles[0])
    }

    @Test
    fun findByName_forOtherUserNotAllowed() {
        val username = "user101"
        val rsp = findByUsername(username, 403, createJwt("user102", "pwuser")).asString()
        assertTrue(rsp.contains("other user not allowed"), rsp.toString())
    }

    @Test
    fun findByName_notFound() {
        val username = "DoesntExist"
        val rsp = findByUsername(username, 404, adminJwt).asString()
        assertTrue(rsp.contains(username), rsp.toString())
    }

    @Test
    fun createFindAddRoleDisable() {
        val username = "user801"
        val pw = "pw801"
        val role = "ROLE_USER"
        lateinit var userJwt: String

        run {
            // when create
            val createRequest = CreateUserRequest(username, pw, true, 30)
            RestCallBuilder(baseUrl, 201).body(createRequest).jwt(adminJwt).post()

            // then
            userJwt = createJwt(username, pw)
            val user = findByUsernameAsUserDto(username, 200, adminJwt)
            assertEquals(0, user.roles.size)
            assertEquals(createRequest.enabled, user.enabled)
            assertEquals(createRequest.loanPeriodInDays, user.loanPeriodInDays)

            findByUsername(username, 403, userJwt) // Because this user has no roles yet!
        }
        run {
            // and add role
            roleCallBuilder(username, role, 201).post()

            // then
            findByUsername(username, 403, userJwt) // outdated jwt still has no access

            // and refresh userJwt succeeds
            userJwt = createJwt(username, pw)

            // and
            val user = findByUsernameAsUserDto(username, 200, userJwt)
            assertEquals(1, user.roles.size)
            assertEquals(role, user.roles[0])
        }
        run {
            // and delete role
            roleCallBuilder(username, role, 200).delete()

            // then
            findByUsername(username, 403, userJwt) // spring security uses this instead of 401

            // and
            val user = findByUsernameAsUserDto(username, 200, adminJwt)
            assertEquals(0, user.roles.size)
        }
        run {
            // and readd role and refresh token
            roleCallBuilder(username, role, 201).post()
            userJwt = createJwt(username, pw)

            // and disable
            RestCallBuilder("${baseUrl}/${username}?enabled=false", 200).jwt(adminJwt).patch()

            // then
            findByUsername(username, 403, userJwt) // coz token has been revoked

            // and refresh userJwt fails
            createJwt(username, pw, 401)

            // and
            val user = findByUsernameAsUserDto(username, 200, adminJwt)
            assertFalse(user.enabled)
        }
    }

    private fun roleCallBuilder(username: String, role: String, expectedStatusCode: Int): RestCallBuilder =
        RestCallBuilder("${baseUrl}/${username}/roles/${role}", expectedStatusCode).jwt(adminJwt)

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
            roleCallBuilder(username, role, 400).post().asString().contains("user does not exist $username")
        )
    }

    @Test
    fun addRole_nonExistingRole() {
        val username = "user101"
        val role = "ROLE_DOES_NOT_EXIST"
        assertTrue(
            roleCallBuilder(username, role, 400).post().asString().contains("non existing role $role")
        )
    }

    @Test
    fun deleteRole_fromNonExistingUser() {
        val username = "userNonExisting"
        val role = "ROLE_USER"
        assertTrue(
            roleCallBuilder(username, role, 404).delete().asString().contains("/users/$username/roles/$role")
        )
    }

    @Test
    fun deleteRole_nonExistingRole() {
        val username = "user101"
        val role = "ROLE_ADMIN"
        assertTrue(
            roleCallBuilder(username, role, 404).delete().asString().contains("/users/$username/roles/$role")
        )
    }
}
