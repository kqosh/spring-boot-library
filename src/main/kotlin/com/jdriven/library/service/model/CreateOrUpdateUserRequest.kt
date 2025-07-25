package com.jdriven.library.service.model

import com.jdriven.library.access.model.UserEntity

data class CreateOrUpdateUserRequest(
    val username: String,
    val password: String,
    val enabled: Boolean,
    val loanPeriodInDays: Int,
    val maxRenewCount: Int,
    val maxLoanCount: Int,
    val outstandingBalanceInCent: Int,
) {

    fun toEntity(): UserEntity = updateEntity(UserEntity())

    fun updateEntity(entity: UserEntity): UserEntity {
        entity.username = username
        entity.password = password
        entity.enabled = enabled
        entity.loanPeriodInDays = loanPeriodInDays
        entity.maxRenewCount = maxRenewCount
        entity.maxLoanCount = maxLoanCount
        entity.outstandingBalanceInCent = outstandingBalanceInCent
        return entity
    }
}
