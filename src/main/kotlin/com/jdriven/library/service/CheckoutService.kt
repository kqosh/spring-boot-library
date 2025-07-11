package com.jdriven.library.service

import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.access.model.CheckoutEntity
import com.jdriven.library.access.model.CheckoutRepository
import com.jdriven.library.access.model.UsersRepository
import com.jdriven.library.service.model.Checkout
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckoutService(
	private val checkoutRepository: CheckoutRepository,
	val usersRepository: UsersRepository,
	val bookRepository: BookRepository)  {

	@Transactional
	fun create(username: String, isbn: String): Checkout? {
		val user = usersRepository.findByUsername(username) ?: return null
		val book = bookRepository.findByIsbn(isbn) ?: return null
		val entity = CheckoutEntity()
		entity.user = user
		entity.book = book
		checkoutRepository.save(entity)
		return Checkout.of(entity)
	}

	@Transactional(readOnly = true)
	fun findByUsername(username: String): List<Checkout>? {
		val user = usersRepository.findByUsername(username) ?: return null
		return checkoutRepository.findByUserAndReturned(user).map { it -> Checkout.of(it) }
	}

	@Transactional
	fun returnBook(username: String, isbn: String): Checkout? {
		val user = usersRepository.findByUsername(username) ?: return null
		val entity = checkoutRepository.findByUserAndReturned(user).filter { it.book.isbn == isbn }.firstOrNull() ?: return null
		entity.returned = true
		return Checkout.of(entity)
	}
}
