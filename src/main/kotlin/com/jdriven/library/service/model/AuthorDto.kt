package com.jdriven.library.service.model

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.BookEntity

data class AuthorDto(
    val name: String,
    val books: List<BookDto> = emptyList()
) {

    companion object {
        fun of(entity: AuthorEntity): AuthorDto = AuthorDto(entity.name, of(entity.books))

        fun of(entities: List<BookEntity>) = entities.map { BookDto.of(it) }
    }
}
