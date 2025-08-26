package com.jdriven.library.access.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.JulianFields

@Entity(name = "Checkout")
@Table(
    name = "checkout",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uc_book_users_checkoutat",
            columnNames = ["book_id", "username", "checkout_at"]
        )
    ]
)
class CheckoutEntity() : AbstractBaseEntity() {

    @Column(name = "checkout_at", nullable = false )
    var checkoutAt: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "due_date", nullable = false )
    var dueDate: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "renew_count", nullable = false )
    var renewCount: Int = 0

    @Column(name = "returned_at")
    var returnedAt: ZonedDateTime? = null

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_checkout_book"), nullable = false)
    lateinit var book: BookEntity

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_checkout_users"), nullable = false, name = "username")
    lateinit var user: UserEntity

    fun overdueFine(finePerDayInCent: Int): Int {
        val diff: Int = mjd(LocalDate.now()) - mjd(dueDate.toLocalDate())
        return if (diff <= 0) 0 else Math.min(diff * finePerDayInCent, book.priceInCent)
    }

    private fun mjd(date: LocalDate): Int = date.getLong(JulianFields.MODIFIED_JULIAN_DAY).toInt()
}