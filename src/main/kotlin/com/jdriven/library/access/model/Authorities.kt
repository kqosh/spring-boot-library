package com.jdriven.library.access.model

import jakarta.persistence.*

@Entity(name = "authorities")//qqqq Authority
@Table(
    name = "authorities",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uc_auth_username",
            columnNames = ["username", "authority"]
        )
    ]
)
class Authorities {//qqqq AuthorityEntity

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @Column(nullable = false, length = 50)
    lateinit var authority: String

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_authorities_users"), nullable = false, name="username")
    lateinit var user: UserEntity
}