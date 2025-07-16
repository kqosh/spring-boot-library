package com.jdriven.library.presentation

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.ZonedDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNotFound(ex: NoResourceFoundException): ResponseEntity<ErrorResponse> {//qqqq not used....
        val errorResponse = ErrorResponse.of(
            HttpStatus.NOT_FOUND,
            ex.message,
            ex.resourcePath
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val httpStatus = getHttpStatus(ex)
        val errorResponse = ErrorResponse.of(httpStatus, ex.message, request.requestURI)
        return ResponseEntity(errorResponse, httpStatus)
    }

    fun getHttpStatus(ex: Exception): HttpStatus =
        when (ex) {
            is org.springframework.web.ErrorResponse -> HttpStatus.valueOf(ex.statusCode.value())
            is IllegalArgumentException -> HttpStatus.BAD_REQUEST
            is IllegalStateException -> HttpStatus.CONFLICT
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
}

data class ErrorResponse(
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String?
) {
    companion object {
        fun of(httpStatus: HttpStatus, message: String? = null, path: String? = null) = ErrorResponse(
            status = httpStatus.value(),
            error = httpStatus.reasonPhrase,
            message = message,
            path = path
        )
//
//        fun getHttpStatus(ex: Exception): HttpStatus =
//            when (ex) {
//            is org.springframework.web.ErrorResponse -> HttpStatus.valueOf(ex.statusCode.value())
//                is IllegalArgumentException -> HttpStatus.BAD_REQUEST
//                is IllegalStateException -> HttpStatus.CONFLICT
//                else -> HttpStatus.INTERNAL_SERVER_ERROR
//            }qqqq
    }
}