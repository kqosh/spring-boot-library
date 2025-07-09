package com.jdriven.library.presentation

import io.restassured.RestAssured.given
import io.restassured.response.ResponseBodyExtractionOptions

object RestAssuredUtils {//qqqq drop

    fun get(url: String, expectedStatusCode: Int, username: String, password: String = "pwuser"): ResponseBodyExtractionOptions {
        return given()
            .log().all()
            . auth().basic(username, password)
            .`when`().get(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }
    fun post(url: String, expectedStatusCode: Int, body: Any, username: String, password: String = "pwuser"): ResponseBodyExtractionOptions {
        return given()
            .log().all()
            . auth().basic(username, password)
            .body(body)
            .`when`().post(url)
            .then()
            .log().all()
            .statusCode(expectedStatusCode)
            .extract().body()
    }
}