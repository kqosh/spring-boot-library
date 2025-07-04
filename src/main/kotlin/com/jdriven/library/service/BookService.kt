package com.jdriven.library.service

import com.jdriven.library.access.model.BookEntity
import com.jdriven.library.access.model.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class BookService(private val repository: BookRepository)  {

	@Transactional(readOnly = true)
	fun find(isbn: String): BookEntity? {
		return if (isbn == "123NotFound") null else repository.findByIsbn(isbn)//qqqq use non existend db value
	}
//
//	@Transactional
//	fun create(book: BookEntity) {
//		return if (isbn == "123NotFound") null else repository.findByIsbn(isbn)
//	}qqqq
}
