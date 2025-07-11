package com.jdriven.library.access.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity(name = "users")
class Users {

    @Id
    @Column(nullable = false, length = 50)
    lateinit var username: String

    @Column(nullable = false, length = 500)
    lateinit var password: String

    @Column(nullable = false)
    var enabled: Boolean?= false

    @OneToMany(mappedBy = "user", targetEntity = Authorities::class)
    var authorities: List<Authorities> = emptyList()

    @OneToMany(mappedBy = "user", targetEntity = CheckoutEntity::class)
    var checkouts: List<CheckoutEntity> = emptyList()
}