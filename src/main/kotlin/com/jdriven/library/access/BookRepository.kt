package com.jdriven.library.access.model

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: CrudRepository<BookEntity, Long> {

    fun findByIsbn(isbn: String): BookEntity?

    // This query usually requires a functional indexes on Book.title and Author.name.
    @Query(
        """
        SELECT b FROM Book b 
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT(:title, '%'))) 
        AND (:authorName IS NULL OR LOWER(b.author.name) LIKE LOWER(CONCAT(:authorName, '%')))
    """
    )
    fun search(authorName: String?, title: String?, pageable: Pageable): Page<BookEntity>

    //qqqq find all limit 50
}