package com.jdriven.library.access.model

import jakarta.persistence.*

//qqqq Nx non-nullebale db field are represented by nullable kotlin fields so faulty data ca be read from the database as well
@Entity(name = "User")
@Table(name = "users")
class UserEntity {

    @Id
    @Column(nullable = false, length = 50)
    lateinit var username: String

    @Column(nullable = false, length = 500)
    lateinit var password: String

    @Column(nullable = false)
    var enabled: Boolean = false

    @Column(name = "loan_period_in_days", nullable = false)
    var loanPeriodInDays: Int = 21

    @OneToMany(mappedBy = "user", targetEntity = AuthorityEntity::class)
    var authorities: List<AuthorityEntity> = emptyList()

    @OneToMany(mappedBy = "user", targetEntity = CheckoutEntity::class)
    var checkouts: List<CheckoutEntity> = emptyList()

    fun addAuthority(authority: AuthorityEntity) {
        if (authorities.isEmpty()) authorities = listOf(authority)
        else authorities = authorities + authority
        authority.user = this
    }
}