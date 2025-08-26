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
    val email: String,
    val phoneNumber: String,
) {

    fun toEntity(): UserEntity = updateEntity(UserEntity())

    fun updateEntity(entity: UserEntity): UserEntity {
        entity.username = username
        entity.password = password
        entity.enabled = enabled
        entity.loanPeriodInDays = loanPeriodInDays
        entity.maxRenewCount = maxRenewCount
        entity.loanLimit = maxLoanCount
        entity.outstandingBalanceInCent = outstandingBalanceInCent
        entity.email = email
        entity.phoneNumber = phoneNumber
        return entity
    }
}
