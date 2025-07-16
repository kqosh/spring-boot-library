package com.jdriven.library.access.model

import jakarta.persistence.*
import java.time.LocalDate

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
class CheckoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(name = "checkout_at", nullable = false )
    var checkoutAt: LocalDate = LocalDate.now()

    @Column(name = "due_date", nullable = false )
    var dueDate: LocalDate = LocalDate.now()

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