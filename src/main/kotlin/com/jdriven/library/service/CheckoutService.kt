package com.jdriven.library.service

import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.access.model.CheckoutEntity
import com.jdriven.library.access.model.CheckoutRepository
import com.jdriven.library.access.model.UserRepository
import com.jdriven.library.service.model.CheckoutDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckoutService(
	private val checkoutRepository: CheckoutRepository,
	private val userRepository: UserRepository,
	private val bookRepository: BookRepository)  {

	@Transactional
	fun create(username: String, isbn: String): CheckoutDto? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		val book = bookRepository.findByIsbn(isbn) ?: throw IllegalArgumentException("book not found: $isbn")

		val currentCheckouts = checkoutRepository.findByBookAndReturned(book, returned = true)
		if (currentCheckouts.size >= book.numberOfCopies) {
			throw IllegalStateException("currently no books available for: $isbn")
		}

		val entity = CheckoutEntity()
		entity.user = user
		entity.book = book
		entity.dueDate = entity.checkoutAt.plusDays(user.loanPeriodInDays.toLong())//qqqq assert in ut
		checkoutRepository.save(entity)
		return CheckoutDto.of(entity)
	}

	@Transactional(readOnly = true)
	fun findByUsername(username: String): List<CheckoutDto>? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		return checkoutRepository.findByUserAndReturned(user).map { it -> CheckoutDto.of(it) }
	}

	@Transactional(readOnly = true)
	fun findByIsbn(isbn: String): List<CheckoutDto>? {
		val book = bookRepository.findByIsbn(isbn) ?: throw IllegalArgumentException("book not found: $isbn")//qqqq eigen ut
		return checkoutRepository.findByBookAndReturned(book).map { it -> CheckoutDto.of(it) }//qqqq ut
	}

	@Transactional
	fun returnBook(username: String, isbn: String): CheckoutDto? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		val entity = checkoutRepository.findByUserAndReturned(user).filter { it.book.isbn == isbn }.firstOrNull() ?: return null
		entity.returned = true
		return CheckoutDto.of(entity)
	}

	@Transactional
	fun renewBook(username: String, isbn: String): CheckoutDto? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		val entity = checkoutRepository.findByUserAndReturned(user).filter { it.book.isbn == isbn }.firstOrNull() ?: return null
		if (entity.renewCount >= user.maxRenewCount) throw IllegalArgumentException("max renew count (${user.maxRenewCount}) exceeded")
		entity.dueDate = entity.dueDate.plusDays(user.loanPeriodInDays.toLong())
		entity.renewCount++
		return CheckoutDto.of(entity)
	}
}
