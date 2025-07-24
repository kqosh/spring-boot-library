package com.jdriven.library.presentation

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authorization.AuthorizationResult
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.ZonedDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponseMessage> {
        val httpStatus = getHttpStatus(ex)
        if (httpStatus.is4xxClientError) {
            logger.warn("Client Error, $httpStatus")
        }
        if (httpStatus.is5xxServerError) {
            logger.error("Server Error, $httpStatus, cause:", ex)
        }
        val errorResponse = ErrorResponseMessage.of(httpStatus, ex.message, request.requestURI)
        return ResponseEntity(errorResponse, httpStatus)
    }

    fun getHttpStatus(ex: Exception): HttpStatus =
        when (ex) {
            is ErrorResponse -> HttpStatus.valueOf(ex.statusCode.value())
            is BadCredentialsException -> HttpStatus.UNAUTHORIZED
            is DisabledException -> HttpStatus.UNAUTHORIZED
            is AccessDeniedException -> HttpStatus.FORBIDDEN
            is CredentialsExpiredException -> HttpStatus.UNAUTHORIZED
            is IllegalArgumentException -> HttpStatus.BAD_REQUEST
            is InvalidDataAccessApiUsageException -> HttpStatus.BAD_REQUEST
            is IllegalStateException -> HttpStatus.CONFLICT
            is AuthorizationResult -> throw ex // use the default exception handler for these
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
}

data class ErrorResponseMessage(
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String?
) {
    companion object {
        fun of(httpStatus: HttpStatus, message: String? = null, path: String? = null) = ErrorResponseMessage(
            status = httpStatus.value(),
            error = httpStatus.reasonPhrase,
            message = message,
            path = path
        )
    }
}
