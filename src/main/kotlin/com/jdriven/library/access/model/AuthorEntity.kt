package com.jdriven.library.access.model

import jakarta.persistence.*

@Entity(name = "author")
@Table(
    name = "author",
    indexes = [
        Index(name = "idx_author_name", columnList = "name")
    ]
)
class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @Column(nullable = false)
    lateinit var name: String

    @OneToMany(mappedBy = "author", targetEntity = BookEntity::class)
    var books: List<BookEntity> = emptyList()
}