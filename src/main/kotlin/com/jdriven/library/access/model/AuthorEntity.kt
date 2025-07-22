package com.jdriven.library.access.model

import jakarta.persistence.*
import org.hibernate.search.engine.backend.types.Sortable
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField

@Entity(name = "Author")
@Table(
    name = "author",
    indexes = [
        Index(name = "idx_author_name", columnList = "name")
    ]
)
class AuthorEntity() : AbstractBaseEntity() {

    @FullTextField
    @KeywordField(name = "name_sort", normalizer = "lowercase", sortable = Sortable.YES)
    @Column(nullable = false, unique = true)
    lateinit var name: String

    @OneToMany(mappedBy = "author", targetEntity = BookEntity::class)
    var books: List<BookEntity> = emptyList()
}