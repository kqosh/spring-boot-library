package com.jdriven.library.presentation

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@ApiResponses(
    value = [
        ApiResponse(
            responseCode = "400", description = "Bad Request, see body for more details", content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = ErrorResponseMessage::class)
            )]
        ),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "403", description = "Forbidden"),
        ApiResponse(responseCode = "404", description = "Not Found", content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponseMessage::class)
        )]),
        ApiResponse(responseCode = "409", description = "Conflict, see body for more details", content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponseMessage::class)
        )]),
        ApiResponse(responseCode = "500", description = "Internal Server Error", content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = ErrorResponseMessage::class)
        )])
    ]
)
annotation class ApiStandardErrors