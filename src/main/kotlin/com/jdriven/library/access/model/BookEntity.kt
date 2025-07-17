package com.jdriven.library.access.model

import jakarta.persistence.*

/**
 * When the last book with this ISBN is decommissioned do not delete tit, instead set its numberOfCopies to 0.
 */
@Entity(name = "Book")
@Table(
    name = "book",
    indexes = [
        Index(name = "idx_book_title", columnList = "title")
    ]
)
class BookEntity() : AbstractBaseEntity() {

    @Column(unique = true, nullable = false)
    var isbn: String? = null

    @Column(nullable = false)
    var title: String? = null

    var publisher: String? = null

    @Column(name = "number_of_copies")
    var numberOfCopies: Int = 10

    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_book_author"))
    var author: AuthorEntity? = null

    @OneToMany(mappedBy = "book", targetEntity = CheckoutEntity::class)
    var checkouts: List<CheckoutEntity> = emptyList()
}