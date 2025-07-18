package com.jdriven.library.presentation

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification

class RestCallBuilder(private val url: String, private val expectedStatusCode: Int) {
    private var _username: String? = null
    private var _password: String? = null
    private var _jwt: String? = null
    private var _body: Any? = null

    fun username(username: String): RestCallBuilder {
        _username = username
        return this
    }

    fun password(password: String): RestCallBuilder {
        _password = password
        return this
    }

    fun jwt(jwt: String): RestCallBuilder {
        _jwt = jwt
        return this
    }

    fun body(body: Any): RestCallBuilder {
        _body = body
        return this
    }

    fun get(): ResponseBodyExtractionOptions =
        givenWhen().get(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()

    fun patch(): ResponseBodyExtractionOptions =
        givenWhen().patch(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()

    fun post(): ResponseBodyExtractionOptions =
        givenWhen()
            .post(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()

    fun put(): ResponseBodyExtractionOptions =
        givenWhen()
            .put(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()

    fun delete(): ResponseBodyExtractionOptions =
        givenWhen().delete(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()

    private fun givenWhen(): RequestSpecification {
        val requestSpec = given()
        if (_username != null) requestSpec.auth().preemptive().basic(_username, _password)
        if (_jwt != null) requestSpec.auth().preemptive().oauth2(_jwt)
//        if (_jwt != null) requestSpec.header("Authorization", "Bearer $_jwt")
        requestSpec.contentType(ContentType.JSON)
        if (_body != null) requestSpec.body(_body)
        return requestSpec.log().all().`when`()
    }
}