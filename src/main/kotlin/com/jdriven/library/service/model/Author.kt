package com.jdriven.library.service.model

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.BookEntity

data class Author(
    val name: String,
    val books: List<Book> = emptyList()
) {

    fun toEntity(): AuthorEntity {//qqqq drop
        val entity = AuthorEntity()
        entity.name = this.name
        entity.books = this.books.map { it -> it.toEntity(entity) }
        return entity
    }

    companion object {
        fun of(entity: AuthorEntity): Author = Author(entity.name, of(entity.books))

        fun of(entities: List<BookEntity>) = entities.map { Book.of(it) }
    }
}
