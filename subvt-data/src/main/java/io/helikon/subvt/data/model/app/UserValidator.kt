package io.helikon.subvt.data.model.app

import io.helikon.subvt.data.model.substrate.AccountId

data class UserValidator(
    val id: Long,
    val userId: Long,
    val networkId: Long,
    val validatorAccountId: AccountId,
)

data class NewUserValidator(
    val networkId: Long,
    val validatorAccountId: AccountId,
)
