package com.jdriven.library.access.model

import jakarta.persistence.*
import java.time.ZonedDateTime

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

    @Column(nullable = false )
    var returned: Boolean = false

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_checkout_book"), nullable = false)
    lateinit var book: BookEntity

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_checkout_users"), nullable = false, name = "username")
    lateinit var user: UserEntity
}