package com.jdriven.library.access.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: CrudRepository<BookEntity, Long> {

    fun findByIsbn(isbn: String): BookEntity?

//
//    fun findByIsbn(isbn: String): BookEntity? {
//        val book = BookEntity() //qqqq find in db
//        book.isbn = isbn
//        return book
//    }
}