package com.jdriven.library.access.model

import jakarta.persistence.*

@Entity(name = "User")
@Table(name = "users")
class UserEntity {

    @Id
    @Column(nullable = false, length = 50)
    lateinit var username: String

    @Column(nullable = false, length = 500)
    lateinit var password: String

    @Column(nullable = false)
    var enabled: Boolean?= false

    @OneToMany(mappedBy = "user", targetEntity = AuthorityEntity::class)
    var authorities: List<AuthorityEntity> = emptyList()

    @OneToMany(mappedBy = "user", targetEntity = CheckoutEntity::class)
    var checkouts: List<CheckoutEntity> = emptyList()
}