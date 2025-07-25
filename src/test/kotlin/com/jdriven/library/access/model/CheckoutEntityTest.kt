package com.jdriven.library.access.model

import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import kotlin.test.assertEquals


class CheckoutEntityTest {

    @Test
    fun overdueFine() {
        val checkout = CheckoutEntity()
        val book = BookEntity()
        book.priceInCent = 30
        checkout.book = book
        checkout.user = UserEntity()

        checkout.dueDate = ZonedDateTime.now().plusDays(1)
        assertEquals(0, checkout.overdueFine(10))

        checkout.dueDate = ZonedDateTime.now().plusDays(0)
        assertEquals(0, checkout.overdueFine(10))

        checkout.dueDate = ZonedDateTime.now().plusDays(-1)
        assertEquals(10, checkout.overdueFine(10))

        checkout.dueDate = ZonedDateTime.now().plusDays(-2)
        assertEquals(20, checkout.overdueFine(10))

        checkout.dueDate = ZonedDateTime.now().plusDays(-3)
        assertEquals(30, checkout.overdueFine(10))

        checkout.dueDate = ZonedDateTime.now().plusDays(-4)
        assertEquals(30, checkout.overdueFine(10))
    }
}