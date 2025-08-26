package com.jdriven.library.service.model

import com.jdriven.library.access.model.UserEntity

data class UserDto(
    val username: String,
    val password: String,
    val enabled: Boolean,
    val loanPeriodInDays: Int,
    val maxRenewCount: Int,
    val loanLimit: Int,
    val outstandingBalanceInCent: Int,
    val email: String,
    val phoneNumber: String,
    val roles: List<String>
) {

    companion object {
        fun of(entity: UserEntity): UserDto = UserDto(
            entity.username,
            entity.password,
            entity.enabled,
            entity.loanPeriodInDays,
            entity.maxRenewCount,
            entity.loanLimit,
            entity.outstandingBalanceInCent,
            entity.email,
            entity.phoneNumber,
            entity.authorities.map { it -> it.authority })
    }
}
