package com.jdriven.library.service

import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.access.model.CheckoutEntity
import com.jdriven.library.access.model.CheckoutRepository
import com.jdriven.library.access.model.MemberRepository
import com.jdriven.library.service.model.Checkout
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckoutService(
	private val checkoutRepository: CheckoutRepository,
	val memberRepository: MemberRepository,
	val bookRepository: BookRepository)  {

	@Transactional
	fun create(memberNumber: String, isbn: String): Checkout? {
		val member = memberRepository.findByNumber(memberNumber) ?: return null
		val book = bookRepository.findByIsbn(isbn) ?: return null
		val entity = CheckoutEntity()
		entity.member = member
		entity.book = book
		checkoutRepository.save(entity)
		return Checkout.of(entity)
	}

	@Transactional(readOnly = true)
	fun findByMember(memberNumber: String): List<Checkout>? {
		val member = memberRepository.findByNumber(memberNumber) ?: return null
		return checkoutRepository.findByMemberAndReturned(member).map { it -> Checkout.of(it) }
	}

	@Transactional
	fun returnBook(memberNumber: String, isbn: String): Checkout? {
		val member = memberRepository.findByNumber(memberNumber) ?: return null
		val entity = checkoutRepository.findByMemberAndReturned(member).filter { it.book.isbn == isbn }.firstOrNull() ?: return null
		entity.returned = true
		return Checkout.of(entity)
	}
}
