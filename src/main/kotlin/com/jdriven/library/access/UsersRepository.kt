package com.jdriven.library.access.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository: CrudRepository<Users, Long> {

    fun findByUsername(name: String): Users?
}