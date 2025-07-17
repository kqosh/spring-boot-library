package com.jdriven.library.service.model

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.BookEntity

data class Author(//qqqq rename *Dto
    val name: String,
    val books: List<Book> = emptyList()
) {

    companion object {
        fun of(entity: AuthorEntity): Author = Author(entity.name, of(entity.books))

        fun of(entities: List<BookEntity>) = entities.map { Book.of(it) }
    }
}
