package com.jdriven.library.access.model

import jakarta.persistence.*

@Entity(name = "Author")
@Table(
    name = "author",
    indexes = [
        Index(name = "idx_author_name", columnList = "name")
    ]
)
class AuthorEntity() : AbstractBaseEntity() {

    @Column(nullable = false, unique = true)
    lateinit var name: String

    @OneToMany(mappedBy = "author", targetEntity = BookEntity::class)
    var books: List<BookEntity> = emptyList()
}