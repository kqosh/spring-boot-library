package com.jdriven.library.access.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckoutRepository: CrudRepository<CheckoutEntity, Long> {

    fun findByUserAndReturned(user: UserEntity, returned: Boolean): List<CheckoutEntity>

    fun findByBookAndReturned(book: BookEntity, returned: Boolean): List<CheckoutEntity>
}