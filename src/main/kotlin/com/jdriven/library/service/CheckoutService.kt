package com.jdriven.library.service

import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.access.model.CheckoutEntity
import com.jdriven.library.access.model.CheckoutRepository
import com.jdriven.library.access.model.UserRepository
import com.jdriven.library.service.model.Checkout
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckoutService(
	private val checkoutRepository: CheckoutRepository,
	private val userRepository: UserRepository,
	private val bookRepository: BookRepository)  {

	private val logger = LoggerFactory.getLogger(this::class.java)

	@Transactional
	fun create(username: String, isbn: String): Checkout? {
		val user = userRepository.findByUsername(username)
		if (user == null) {
			throw IllegalArgumentException("user not found: $username")//qqqq eigen ut
		}
		val book = bookRepository.findByIsbn(isbn)
		if (book == null) {
			throw IllegalArgumentException("book not found: $isbn")//qqqq eigen ut
		}
		val currentCheckouts = checkoutRepository.findByBookAndReturned(book, returned = true)
		if (currentCheckouts.size >= book.numberOfCopies) {
			throw IllegalStateException("no books available for: $isbn")//qqqq eigen ut
		}

		val entity = CheckoutEntity()
		entity.user = user
		entity.book = book
		checkoutRepository.save(entity)
		return Checkout.of(entity)
	}

	@Transactional(readOnly = true)
	fun findByUsername(username: String): List<Checkout>? {
		val user = userRepository.findByUsername(username) ?: return null
		return checkoutRepository.findByUserAndReturned(user).map { it -> Checkout.of(it) }
	}

	@Transactional
	fun returnBook(username: String, isbn: String): Checkout? {
		val user = userRepository.findByUsername(username)
		if (user == null) {
			throw IllegalArgumentException("user not found: $username")
		}
		val entity = checkoutRepository.findByUserAndReturned(user).filter { it.book.isbn == isbn }.firstOrNull() ?: return null
		entity.returned = true
		return Checkout.of(entity)
	}
}
