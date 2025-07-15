package com.jdriven.library.service.model

import com.jdriven.library.access.model.UserEntity

data class CreateUserRequest(//qqqq rename to User
    val username: String,
    val password: String,
    val enabled: Boolean,
    val loanPeriodInDays: Int,
) {

    fun toEntity(): UserEntity {
        val entity = UserEntity()
        entity.username = username
        entity.password = password
        entity.enabled = enabled
        entity.loanPeriodInDays = loanPeriodInDays
        return entity
    }
}
