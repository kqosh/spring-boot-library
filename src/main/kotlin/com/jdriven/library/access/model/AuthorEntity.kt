package com.jdriven.library.access.model

import jakarta.persistence.*
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField

@Entity(name = "Author")
@Table(
    name = "author",
    indexes = [
        Index(name = "idx_author_name", columnList = "name")
    ]
)
class AuthorEntity() : AbstractBaseEntity() {

    @FullTextField
    @Column(nullable = false, unique = true)
    lateinit var name: String

    @OneToMany(mappedBy = "author", targetEntity = BookEntity::class)
    var books: List<BookEntity> = emptyList()
}