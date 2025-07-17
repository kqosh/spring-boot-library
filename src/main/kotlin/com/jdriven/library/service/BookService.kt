package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.AuthorRepository
import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.service.model.BookDto
import com.jdriven.library.service.model.PaginatedResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(private val bookRepository: BookRepository, private val authorRepository: AuthorRepository)  {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@Transactional(readOnly = true)
	fun find(isbn: String): BookDto? {
		return bookRepository.findByIsbn(isbn)?.let { BookDto.of(it) }
	}

	@Transactional
	fun create(book: BookDto): BookDto? {
		var authorEntity = findOrCreate(book)
		val bookEntity = book.toEntity(authorEntity)
		return BookDto.of(bookRepository.save(bookEntity))
	}

	private fun findOrCreate(book: BookDto): AuthorEntity {
		var authorEntity = authorRepository.findByName(book.authorName!!)
		if (authorEntity == null) {
			authorEntity = AuthorEntity()
			authorEntity.name = book.authorName!!
			authorRepository.save(authorEntity)
		}
		return authorEntity
	}

	@Transactional
	fun update(isbn: String, book: BookDto): BookDto? {
		val bookEntity = bookRepository.findByIsbn(isbn) ?: return null
		var authorEntity = findOrCreate(book)
		return BookDto.of(book.updateEntity(bookEntity, authorEntity))
	}

	@Transactional
	fun delete(isbn: String): BookDto? {
		val entity = bookRepository.findByIsbn(isbn) ?: return null
		bookRepository.delete(entity)
		return BookDto.of(entity)
	}

	@Transactional(readOnly = true)
	fun search(authorName: String?, title: String?, pageIndex: Int, pageSize: Int = 20): PaginatedResponse<BookDto> {
		if (authorName.isNullOrEmpty() && title.isNullOrEmpty()) throw IllegalArgumentException("authorName and title must not be both empty")
		val pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("author.name", "title"))
		val page = bookRepository.search(authorName, title, pageRequest)
		val books = page.content.map { it -> BookDto.of(it)}
		return PaginatedResponse(content = books, pageIndex, pageSize, page.totalElements, page.totalPages)
	}
}
