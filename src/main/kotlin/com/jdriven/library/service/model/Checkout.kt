package com.jdriven.library.service.model

import com.jdriven.library.access.model.CheckoutEntity
import java.time.ZonedDateTime

data class Checkout(//qqqq rename *Dto
    val checkoutAt: ZonedDateTime,
    val dueDate: ZonedDateTime,
    val returned: Boolean,
    val renewCount: Int,
    val book: Book
) {

    companion object {
        fun of(entity: CheckoutEntity): Checkout =
            Checkout(entity.checkoutAt, entity.dueDate, entity.returned, entity.renewCount, Book.of(entity.book))
    }
}
