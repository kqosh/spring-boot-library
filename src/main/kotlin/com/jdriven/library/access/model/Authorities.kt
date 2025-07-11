package com.jdriven.library.access.model

import jakarta.persistence.*

@Entity(name = "authorities")
@Table(
    name = "authorities",
    uniqueConstraints = [
        UniqueConstraint(
            name = "ix_auth_username", //qqqq uc_*
            columnNames = ["username", "authority"]
        )
    ]
)
class Authorities {
//
//    @Column(nullable = false, length = 50)
//    lateinit var username: String


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @Column(nullable = false, length = 50)
    lateinit var authority: String

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_authorities_users"), nullable = false, name="username")
    lateinit var user: Users

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
     */
}