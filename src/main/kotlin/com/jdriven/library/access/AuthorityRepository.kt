package com.jdriven.library.access.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository: CrudRepository<AuthorityEntity, Long> {

    fun findByAuthority(authority: String): AuthorEntity?
}