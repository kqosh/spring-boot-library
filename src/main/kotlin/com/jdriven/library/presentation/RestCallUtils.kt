package com.jdriven.library.presentation

import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponse
import org.springframework.web.server.ResponseStatusException

object RestCallUtils {

    fun translateException(ex: Exception): Exception {
        return when (ex) {
            is ErrorResponse -> ex
            is IllegalArgumentException -> ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message!!)
            is IllegalStateException -> ResponseStatusException(HttpStatus.CONFLICT, ex.message!!)
            else -> ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.message!!)
        }
    }
}