package com.jdriven.library.access.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity(name = "users")
class Users {

    @Id
    @Column(nullable = false, length = 50)
    // @Collate is de Hibernate-manier om case-insensitivity af te dwingen,
    // vergelijkbaar met VARCHAR_IGNORECASE in H2.qqqq eng
//    @Collate("utf8mb4_general_ci") // Voorbeeld voor MySQLqqqq
    lateinit var username: String

    @Column(nullable = false, length = 500)
    lateinit var password: String

    @Column(nullable = false)
    var enabled: Boolean?= false

    @OneToMany(mappedBy = "user", targetEntity = Authorities::class)
    var authorities: List<Authorities> = emptyList()
//
//    @OneToMany(mappedBy = "member", targetEntity = CheckoutEntity::class)
//    var checkouts: List<CheckoutEntity> = emptyList()qqqq

    /*qqqq
    CREATE TABLE users (
    username VARCHAR_IGNORECASE(50) NOT NULL PRIMARY KEY,
    password VARCHAR(500) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities (
    username VARCHAR_IGNORECASE(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
);
CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);


import jakarta.persistence.*
import org.hibernate.annotations.Collate

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(length = 50)
    // @Collate is de Hibernate-manier om case-insensitivity af te dwingen,
    // vergelijkbaar met VARCHAR_IGNORECASE in H2.
    @Collate("utf8mb4_general_ci") // Voorbeeld voor MySQL
    var username: String,

    @Column(length = 500, nullable = false)
    var password: String,

    @Column(nullable = false)
    var enabled: Boolean,

    // Deze annotatie mapt de 'authorities' naar een aparte tabel.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "authorities", // De naam van de join-tabel
        joinColumns = [JoinColumn(name = "username")] // De foreign key kolom
    )
    @Column(name = "authority", nullable = false) // De naam van de kolom met de autoriteit-string
    var authorities: Set<String> = setOf()
)
     */
}