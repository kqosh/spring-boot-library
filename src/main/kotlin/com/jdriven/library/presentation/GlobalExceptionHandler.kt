package com.jdriven.library.presentation

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.ZonedDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val httpStatus = getHttpStatus(ex)
        val errorResponse = ErrorResponse.of(httpStatus, ex.message, request.requestURI)
        return ResponseEntity(errorResponse, httpStatus)
    }

    fun getHttpStatus(ex: Exception): HttpStatus =
        when (ex) {
            is org.springframework.web.ErrorResponse -> HttpStatus.valueOf(ex.statusCode.value())
            is BadCredentialsException -> HttpStatus.UNAUTHORIZED
            is DisabledException -> HttpStatus.UNAUTHORIZED
            is AccessDeniedException -> HttpStatus.FORBIDDEN
            is CredentialsExpiredException -> HttpStatus.UNAUTHORIZED
            is IllegalArgumentException -> HttpStatus.BAD_REQUEST
            is IllegalStateException -> HttpStatus.CONFLICT
            is AuthorizationResult -> throw ex // use the default exception handler for these
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
    }
}