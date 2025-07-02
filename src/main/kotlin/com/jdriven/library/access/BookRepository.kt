package com.jdriven.library.access.model

import org.springframework.stereotype.Repository

@Repository
class BookRepository {

    fun find(isbn: String): BookEntity? {
        val book = BookEntity() //qqqq find in db
        book.isbn = isbn
        return book
    }
}