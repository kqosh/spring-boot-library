package com.jdriven.library.access.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository: CrudRepository<AuthorEntity, Long> {

    fun findByName(name: String): AuthorEntity?
}