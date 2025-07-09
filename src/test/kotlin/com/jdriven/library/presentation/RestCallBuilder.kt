package com.jdriven.library.presentation

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ResponseBodyExtractionOptions

class RestCallBuilder(private val url: String, private val expectedStatusCode: Int) {
    private var _username: String? = null
    private var _password: String? = null
    private var _body: Any? = null

    fun username(username: String): RestCallBuilder {
        _username = username
        return this
    }

    fun password(password: String): RestCallBuilder {
        _password = password
        return this
    }

    fun body(body: Any): RestCallBuilder {
        _body = body
        return this
    }

    fun get(): ResponseBodyExtractionOptions {
        return given()
            .log().all()
            . auth().basic(_username, _password)
            .`when`().get(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }

    fun post(): ResponseBodyExtractionOptions {
        val requestSpec = given()
            .log().all()
            . auth().basic(_username, _password)
            .contentType(ContentType.JSON)

        if (_body != null) requestSpec.body(_body)

        return requestSpec
            .`when`().post(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }

    fun delete() {
        given()
            .log().all()
            . auth().basic(_username, _password)
            .`when`().delete(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
    }
}