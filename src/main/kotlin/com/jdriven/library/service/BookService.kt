package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.AuthorRepository
import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.service.model.Book
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val bookRepository: BookRepository, private val authorRepository: AuthorRepository)  {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@Transactional(readOnly = true)
	fun find(isbn: String): Book? {
		return bookRepository.findByIsbn(isbn)?.let { Book.of(it) }
	}

	@Transactional
	fun create(book: Book): Book? {
		var authorEntity = findOrCreate(book)
		val bookEntity = book.toEntity(authorEntity)
		return Book.of(bookRepository.save(bookEntity))
	}

	private fun findOrCreate(book: Book): AuthorEntity {
		var authorEntity = authorRepository.findByName(book.authorName!!)
		if (authorEntity == null) {
			authorEntity = AuthorEntity()
			authorEntity.name = book.authorName!!
			authorRepository.save(authorEntity)//qqqq eigen ut for post en put
		}
		return authorEntity
	}

	@Transactional
	fun update(book: Book): Book? {
		val bookEntity = bookRepository.findByIsbn(book.isbn) ?: return null
		var authorEntity = findOrCreate(book)
		return Book.of(book.updateEntity(bookEntity, authorEntity))
	}

	@Transactional
	fun delete(isbn: String): Book? {
		val entity = bookRepository.findByIsbn(isbn) ?: return null
		bookRepository.delete(entity)
		return Book.of(entity)
	}

	@Transactional(readOnly = true)
	fun search(authorName: String?, title: String?): List<Book> {
		if (authorName.isNullOrEmpty() && title.isNullOrEmpty()) throw IllegalArgumentException("authorName and title must both be empyt")
		return bookRepository.search(authorName, title).map { it -> Book.of(it)}
	}
}
