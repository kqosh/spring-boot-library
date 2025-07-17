package com.jdriven.library.service.model

import com.jdriven.library.access.model.CheckoutEntity
import java.time.ZonedDateTime

data class CheckoutDto(
    val checkoutAt: ZonedDateTime,
    val dueDate: ZonedDateTime,
    val returned: Boolean,
    val renewCount: Int,
    val book: BookDto
) {

    companion object {
        fun of(entity: CheckoutEntity): CheckoutDto =
            CheckoutDto(entity.checkoutAt, entity.dueDate, entity.returned, entity.renewCount, BookDto.of(entity.book))
    }
}
