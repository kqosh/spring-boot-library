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
        var requestSpec = given()
            .log().all()
            . auth().basic(_username, _password)
            .contentType(ContentType.JSON)

        if (_body != null) requestSpec = requestSpec.body(_body)

        return requestSpec
            .`when`().post(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }
//
//    fun request(methode: HttpMethod): ResponseBodyExtractionOptions {
//        var requestSpec = given()
//            .log().all()
//            . auth().basic(_username, _password)
//            .contentType(ContentType.JSON)
//
//        if (_body != null) requestSpec = requestSpec.body(_body)
//
//
//        var qqqq: ResponseOptions? = null
//        when(methode) {
//            HttpMethod.GET -> qqqq = requestSpec.`when`().get(url)
//            HttpMethod.DELETE -> requestSpec.delete(url)
//            HttpMethod.HEAD -> requestSpec.head(url)
//            HttpMethod.PATCH -> requestSpec.patch(url)
//            HttpMethod.POST -> requestSpec.post(url)
//            HttpMethod.PUT -> requestSpec.put(url)
//            else -> throw IllegalArgumentException("method=$methode")
//        }
//
//        return requestSpec.`when`().post(url)
//            .then()
//            .log().all()
//            .statusCode(expectedStatusCode)
//            .extract().body()
//    }

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