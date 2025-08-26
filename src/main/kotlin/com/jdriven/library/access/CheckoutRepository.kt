package com.jdriven.library.access.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckoutRepository: CrudRepository<CheckoutEntity, Long> {

    fun findByUserAndReturnedAtIsNull(user: UserEntity): List<CheckoutEntity>

    fun findByBookAndReturnedAtIsNull(book: BookEntity): List<CheckoutEntity>
}