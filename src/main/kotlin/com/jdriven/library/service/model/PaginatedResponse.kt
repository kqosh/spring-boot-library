package com.jdriven.library.service.model

import org.springframework.data.domain.Page

data class PaginatedResponse<T>(
    val content: List<T>,
    val currentPage: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int
) {

    companion object {
        fun <T> of(page: Page<T>): PaginatedResponse<T> =
            PaginatedResponse(page.content, page.number, page.size, page.totalElements, page.totalPages)
    }
}
