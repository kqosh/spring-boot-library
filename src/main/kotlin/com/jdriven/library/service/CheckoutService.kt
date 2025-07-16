package com.jdriven.library.service

import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.access.model.CheckoutEntity
import com.jdriven.library.access.model.CheckoutRepository
import com.jdriven.library.access.model.UserRepository
import com.jdriven.library.service.model.Checkout
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CheckoutService(
	private val checkoutRepository: CheckoutRepository,
	private val userRepository: UserRepository,
	private val bookRepository: BookRepository)  {

	@Transactional
	fun create(username: String, isbn: String): Checkout? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")//qqqq eigen ut
		val book = bookRepository.findByIsbn(isbn) ?: throw IllegalArgumentException("book not found: $isbn")//qqqq eigen ut

		val currentCheckouts = checkoutRepository.findByBookAndReturned(book, returned = true)
		if (currentCheckouts.size >= book.numberOfCopies) {
			throw IllegalStateException("no books available for: $isbn")//qqqq eigen ut
		}

		val entity = CheckoutEntity()
		entity.user = user
		entity.book = book
		entity.dueDate = entity.checkoutAt.plusDays(user.loanPeriodInDays!!.toLong())//qqqq assert in ut
		checkoutRepository.save(entity)
		return Checkout.of(entity)
	}

	@Transactional(readOnly = true)
	fun findByUsername(username: String): List<Checkout>? {
		val user = userRepository.findByUsername(username) ?: return null
		return checkoutRepository.findByUserAndReturned(user).map { it -> Checkout.of(it) }
	}

	@Transactional(readOnly = true)
	fun findByIsbn(isbn: String): List<Checkout>? {
		val book = bookRepository.findByIsbn(isbn) ?: throw IllegalArgumentException("book not found: $isbn")//qqqq eigen ut
		return checkoutRepository.findByBookAndReturned(book).map { it -> Checkout.of(it) }//qqqq ut
	}

	@Transactional
	fun returnBook(username: String, isbn: String): Checkout? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		val entity = checkoutRepository.findByUserAndReturned(user).filter { it.book.isbn == isbn }.firstOrNull() ?: return null
		entity.returned = true
		return Checkout.of(entity)
	}

	@Transactional
	fun renewBook(username: String, isbn: String): Checkout? {//qqqq ut
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		val entity = checkoutRepository.findByUserAndReturned(user).filter { it.book.isbn == isbn }.firstOrNull() ?: return null
		if (entity.renewCount >= user.maxRenewCount) throw IllegalArgumentException("max renew count (${user.maxRenewCount} exceeded")
		entity.dueDate = entity.dueDate.plusDays(user.loanPeriodInDays!!.toLong())
		entity.renewCount++
		return Checkout.of(entity)
	}
}
