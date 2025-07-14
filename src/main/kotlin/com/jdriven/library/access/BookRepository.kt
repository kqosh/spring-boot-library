package com.jdriven.library.access.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: CrudRepository<BookEntity, Long> {

    fun findByIsbn(isbn: String): BookEntity?
//
//    fun search(authorName: String?, title: String?): List<BookEntity> {
//        if (authorName.isNullOrEmpty() && title.isNullOrEmpty()) throw IllegalArgumentException("authorName and title must both be empyt")
//        return searchqqqq(authorName, title)
//    }

    //qqqq func index
    @Query(
        """
        SELECT b FROM Book b 
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT(:title, '%'))) 
        AND (:authorName IS NULL OR LOWER(b.author.name) LIKE LOWER(CONCAT(:authorName, '%')))
    """
    )
    fun search(authorName: String?, title: String?): List<BookEntity>
}