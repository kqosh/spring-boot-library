package com.jdriven.library.access.model

import jakarta.persistence.*

@Entity
@Table(
    name = "book",
    indexes = [
        Index(name = "idx_book_author_name", columnList = "authorName"),
        Index(name = "idx_book_title", columnList = "title")
    ]
)
class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @Column(unique = true, nullable = false)
    var isbn: String? = null

    @Column(nullable = false, name = "author_name")//qqqq fk to Author
    var authorName: String? = null

    @Column(nullable = false)
    var title: String? = null

    var publisher: String? = null

    //qqqq fk to vestiging=location
    //qqqq borrowedBy fk to member
    //qqqq registrations: List<Member>
}