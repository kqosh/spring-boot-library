package com.jdriven.library.service.model

import com.jdriven.library.access.model.CheckoutEntity
import java.time.ZonedDateTime

data class CheckoutDto(
    val checkoutAt: ZonedDateTime,
    val dueDate: ZonedDateTime,
    val returned_at: ZonedDateTime?,
    val renewCount: Int,
    val book: BookDto,
    val user: UserDto
) {

    companion object {
        fun of(entity: CheckoutEntity): CheckoutDto =
            CheckoutDto(
                entity.checkoutAt,
                entity.dueDate,
                entity.returnedAt,
                entity.renewCount,
                BookDto.of(entity.book),
                UserDto.of(entity.user)
            )
    }
}
