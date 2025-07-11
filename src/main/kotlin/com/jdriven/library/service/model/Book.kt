package com.jdriven.library.service.model

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.BookEntity

data class Book(
    val isbn: String,
    var authorName: String? = null,
    var title: String? = null,
    var publisher: String? = null
) {

    fun toEntity(authorEntity: AuthorEntity): BookEntity = updateEntity(BookEntity(), authorEntity)

    fun updateEntity(bookEntity: BookEntity, authorEntity: AuthorEntity): BookEntity {
        bookEntity.isbn = this.isbn
        bookEntity.author = authorEntity
        bookEntity.title = this.title
        bookEntity.publisher = this.publisher
        return bookEntity
    }

    companion object {
        fun of(entity: BookEntity): Book = Book(entity.isbn!!, entity.author?.name, entity.title, entity.publisher)
    }
}
