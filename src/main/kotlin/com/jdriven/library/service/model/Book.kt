package com.jdriven.library.service.model

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.BookEntity

data class Book(//qqqq rename *Dto
    val isbn: String,
    var authorName: String? = null,
    var title: String? = null,
    var publisher: String? = null,
    var numberOfCopies: Int
) {

    fun toEntity(authorEntity: AuthorEntity): BookEntity = updateEntity(BookEntity(), authorEntity)

    fun updateEntity(bookEntity: BookEntity, authorEntity: AuthorEntity): BookEntity {
        bookEntity.isbn = this.isbn
        bookEntity.author = authorEntity
        bookEntity.title = this.title
        bookEntity.publisher = this.publisher
        bookEntity.numberOfCopies = this.numberOfCopies
        return bookEntity
    }

    companion object {
        fun of(entity: BookEntity): Book = Book(entity.isbn!!, entity.author?.name, entity.title, entity.publisher, entity.numberOfCopies)
    }
}
