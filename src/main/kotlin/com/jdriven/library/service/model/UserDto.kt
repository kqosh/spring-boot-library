package com.jdriven.library.service.model

import com.jdriven.library.access.model.UserEntity

data class UserDto(
    val username: String,
    val password: String,
    val enabled: Boolean,
    val loanPeriodInDays: Int,
    val maxRenewCount: Int,
    val maxLoanCount: Int,
    val outstandingBalanceInCent: Int,
    val roles: List<String>
) {

    companion object {
        fun of(entity: UserEntity): UserDto = UserDto(
            entity.username,
            entity.password,
            entity.enabled,
            entity.loanPeriodInDays,
            entity.maxRenewCount,
            entity.maxLoanCount,
            entity.outstandingBalanceInCent,
            entity.authorities.map { it -> it.authority })
    }
}
