package com.jdriven.library.access.model

import jakarta.persistence.*
import org.hibernate.search.engine.backend.types.Sortable
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField

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
@Indexed
class BookEntity() : AbstractBaseEntity() {

    @Column(unique = true, nullable = false)
    lateinit var isbn: String

    @FullTextField//qqqq(analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    @KeywordField(name = "title_sort", normalizer = "lowercase", sortable = Sortable.YES)
    @Column
    var title: String? = null

    var publisher: String? = null

    @Column(name = "number_of_copies")
    var numberOfCopies: Int = 10

    @IndexedEmbedded
    @ManyToOne
    @JoinColumn(foreignKey = ForeignKey(name = "fk_book_author"))
    var author: AuthorEntity? = null

    @OneToMany(mappedBy = "book", targetEntity = CheckoutEntity::class)
    var checkouts: List<CheckoutEntity> = emptyList()
}