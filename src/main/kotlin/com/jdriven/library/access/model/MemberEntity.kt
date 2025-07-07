package com.jdriven.library.access.model

import jakarta.persistence.*

@Entity(name = "member")
@Table(
    name = "member",
    indexes = [
        Index(name = "idx_member_name", columnList = "name")
    ]
)
class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @Column(nullable = false, unique = true)
    lateinit var number: String

    @Column(nullable = false)
    lateinit var name: String

    @OneToMany(mappedBy = "member", targetEntity = CheckoutEntity::class)
    var checkouts: List<CheckoutEntity> = emptyList()
}