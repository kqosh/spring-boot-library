package com.jdriven.library.access.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository: CrudRepository<AuthorityEntity, Long> {

    @Query(
        """
        SELECT a FROM Authority a
        WHERE a.user.username = :username
        AND a.authority = :authority
    """
    )
    fun findByUsernameAndAuthority(username: String, authority: String): AuthorityEntity?
}