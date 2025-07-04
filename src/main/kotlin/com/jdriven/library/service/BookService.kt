package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorRepository
import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.service.model.Book
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val bookRepository: BookRepository, private val authorRepository: AuthorRepository)  {

	@Transactional(readOnly = true)
	fun find(isbn: String): Book? {
		return bookRepository.findByIsbn(isbn)?.let { Book.of(it) } ?: null
	}

	@Transactional
	fun create(book: Book): Book? {
		val authorEntity = authorRepository.findByName(book.authorName!!)
		if (authorEntity == null) return null

		val bookEntity = book.toEntity(authorEntity)
		return Book.of(bookRepository.save(bookEntity))
	}
}
