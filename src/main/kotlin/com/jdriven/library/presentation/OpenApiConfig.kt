//package com.jdriven.library.presentation
//
//import io.swagger.v3.oas.models.responses.ApiResponse
//import io.swagger.v3.oas.models.responses.ApiResponses
//import org.springdoc.core.customizers.OpenApiCustomizer
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.HttpStatus
//
//@Configuration
//class OpenApiConfig {
//qqqq
//    @Bean
//    fun globalApiResponses(): OpenApiCustomizer {
//        return OpenApiCustomizer { openApi ->
//            // qqqq
//            val unauthorized = ApiResponse().description(HttpStatus.UNAUTHORIZED.reasonPhrase)
//            val forbidden = ApiResponse().description(HttpStatus.FORBIDDEN.reasonPhrase)
//            val notFound = ApiResponse().description(HttpStatus.NOT_FOUND.reasonPhrase)
//            //qqqq more?
//
//            openApi.paths.values.forEach { pathItem ->
//                pathItem.get?.let { operation ->
//                    val responses = operation.responses ?: ApiResponses()
//                    responses.addApiResponse("401", unauthorized)
//                    responses.addApiResponse("403", forbidden)
//                    responses.addApiResponse("404", notFound)
//                    operation.responses = responses
//                }
//                pathItem.patch?.let { operation ->
//                    val responses = operation.responses ?: ApiResponses()
//                    responses.addApiResponse("401", unauthorized)
//                    responses.addApiResponse("403", forbidden)
//                    responses.addApiResponse("404", notFound)
//                    operation.responses = responses
//                }
//                pathItem.post?.let { operation ->
//                    val responses = operation.responses ?: ApiResponses()
//                    responses.addApiResponse("401", unauthorized)
//                    responses.addApiResponse("403", forbidden)
//                    operation.responses = responses
//                }
//                pathItem.post?.let { operation ->
//                    val responses = operation.responses ?: ApiResponses()
//                    responses.addApiResponse("401", unauthorized)
//                    responses.addApiResponse("403", forbidden)
//                    responses.addApiResponse("404", notFound)
//                    operation.responses = responses
//                }
//                pathItem.delete?.let { operation ->
//                    val responses = operation.responses ?: ApiResponses()
//                    responses.addApiResponse("401", unauthorized)
//                    responses.addApiResponse("403", forbidden)
//                    responses.addApiResponse("404", notFound)
//                    operation.responses = responses
//                }
//            }
//        }
//    }
//}