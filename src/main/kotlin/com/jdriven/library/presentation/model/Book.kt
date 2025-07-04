package com.jdriven.library.presentation.model

import com.jdriven.library.access.model.BookEntity

data class Book(
    val isbn: String,
    var authorName: String? = null,
    var title: String? = null,
    var publisher: String? = null
) {

    companion object {
        fun of(entity: BookEntity): Book = Book(entity.isbn!!, entity.author?.name, entity.title, entity.publisher)
    }
}
