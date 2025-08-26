package com.jdriven.library.service

import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.access.model.CheckoutEntity
import com.jdriven.library.access.model.CheckoutRepository
import com.jdriven.library.access.model.UserRepository
import com.jdriven.library.service.model.CheckoutDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class CheckoutService(
	private val checkoutRepository: CheckoutRepository,
	private val userRepository: UserRepository,
	private val bookRepository: BookRepository,
	@Value("\${fine.per.day.in.cent}") private val finePerDayInCent: Int,
)  {

	@Transactional
	fun create(username: String, isbn: String): CheckoutDto? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		val book = bookRepository.findByIsbn(isbn) ?: throw IllegalArgumentException("book not found: $isbn")

		val currentCheckoutsForBook = checkoutRepository.findByBookAndReturnedAtIsNull(book)
		if (currentCheckoutsForBook.size >= book.numberOfCopies) {
			throw IllegalStateException("currently no books available for: $isbn")
		}

		val currentCheckoutsFoUser = checkoutRepository.findByUserAndReturnedAtIsNull(user)
		if (currentCheckoutsFoUser.size >= user.loanLimit) {
			throw IllegalArgumentException("max number of loans reached: $username ${user.loanLimit}")
		}
		if (currentCheckoutsFoUser.count { it -> it.book.isbn == isbn } >= 1) {
			throw IllegalArgumentException("max one copy can be borrowed: $username $isbn")
		}

		if (user.outstandingBalanceInCent > 0) {
			throw IllegalArgumentException("outstanding balance must be payed first")
		}

		val entity = CheckoutEntity()
		entity.user = user
		entity.book = book
		entity.dueDate = entity.checkoutAt.plusDays(user.loanPeriodInDays.toLong())
		checkoutRepository.save(entity)
		return CheckoutDto.of(entity)
	}

	@Transactional(readOnly = true)
	fun findByUsername(username: String): List<CheckoutDto>? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		return checkoutRepository.findByUserAndReturnedAtIsNull(user).map { it -> CheckoutDto.of(it) }
	}

	@Transactional(readOnly = true)
	fun findByIsbn(isbn: String): List<CheckoutDto>? {
		val book = bookRepository.findByIsbn(isbn) ?: throw IllegalArgumentException("book not found: $isbn")
		return checkoutRepository.findByBookAndReturnedAtIsNull(book).map { it -> CheckoutDto.of(it) }
	}

	@Transactional
	fun returnBook(username: String, isbn: String): CheckoutDto? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		val entity = checkoutRepository.findByUserAndReturnedAtIsNull(user).filter { it.book.isbn == isbn }.firstOrNull() ?: return null

		val overdueFine = entity.overdueFine(finePerDayInCent)
		if (overdueFine > 0) user.outstandingBalanceInCent += overdueFine

		entity.returnedAt = ZonedDateTime.now()
		return CheckoutDto.of(entity)
	}

	@Transactional
	fun renewBook(username: String, isbn: String): CheckoutDto? {
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user not found: $username")
		val entity = checkoutRepository.findByUserAndReturnedAtIsNull(user).filter { it.book.isbn == isbn }.firstOrNull() ?: return null
		if (entity.renewCount >= user.maxRenewCount) throw IllegalArgumentException("max renew count (${user.maxRenewCount}) exceeded")

		entity.dueDate = entity.dueDate.plusDays(user.loanPeriodInDays.toLong())
		entity.renewCount++
		return CheckoutDto.of(entity)
	}
}
