package com.jdriven.library.service.model

import com.jdriven.library.access.model.CheckoutEntity
import java.time.LocalDate

data class Checkout(
    val checkoutAt: LocalDate,
    val returned: Boolean,
    val book: Book
) {

    companion object {
        fun of(entity: CheckoutEntity): Checkout = Checkout(entity.checkoutAt, returned = entity.returned, Book.of(entity.book))
    }
}
