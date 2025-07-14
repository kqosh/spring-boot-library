package com.jdriven.library.access.model

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository: CrudRepository<AuthorEntity, Long> {

    fun findByName(name: String): AuthorEntity?

    // This query usually requires functional indexes on Book.title and Author.name.
    @Query(
        """
        SELECT a FROM Author a 
        WHERE (:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT(:name, '%')))
    """
    )
    fun search(name: String, pageable: Pageable): Page<AuthorEntity>
}