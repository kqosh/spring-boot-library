package com.jdriven.library.access.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: CrudRepository<MemberEntity, Long> {

    fun findByName(name: String): MemberEntity?

    fun findByNumber(number: String): MemberEntity?
}