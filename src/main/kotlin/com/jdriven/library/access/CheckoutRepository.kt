package com.jdriven.library.access.model

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckoutRepository: CrudRepository<CheckoutEntity, Long> {

    fun findByMemberAndReturned(member: MemberEntity, returned: Boolean = false): List<CheckoutEntity>

    fun findByBookAndReturned(member: MemberEntity, returned: Boolean = false): List<CheckoutEntity>
}