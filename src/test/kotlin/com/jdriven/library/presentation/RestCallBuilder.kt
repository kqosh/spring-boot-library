package com.jdriven.library.presentation

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification

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
        return givenWhen().get(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }

    fun patch(): ResponseBodyExtractionOptions {
        return givenWhen().patch(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }

    fun post(): ResponseBodyExtractionOptions {
        return givenWhen()
            .post(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }

    fun put(): ResponseBodyExtractionOptions {
        return givenWhen()
            .put(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }

    fun delete() {
        givenWhen().delete(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
    }

    private fun givenWhen(): RequestSpecification {
        val requestSpec = given().log().all()
        if (_username != null) requestSpec.auth().basic(_username, _password)
        requestSpec.contentType(ContentType.JSON)
        if (_body != null) requestSpec.body(_body)
        return requestSpec.`when`()
    }
}