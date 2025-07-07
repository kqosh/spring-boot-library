package com.jdriven.library.access.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity(name = "checkout")
@Table(
    name = "checkout",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uc_book_member_checkoutat",
            columnNames = ["book_id", "member_id", "checkout_at"]
        )
    ]
)
class CheckoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(name = "checkout_at", nullable = false )
    var checkoutAt: LocalDate = LocalDate.now()

    @Column(nullable = false )
    var returned: Boolean = false

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_checkout_book"), nullable = false)
    lateinit var book: BookEntity

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_checkout_member"), nullable = false)
    lateinit var member: MemberEntity
}