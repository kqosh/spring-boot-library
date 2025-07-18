package com.jdriven.library.service.model

data class PaginatedResponse<T>(
    val content: List<T>,
    val currentPage: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int
)
