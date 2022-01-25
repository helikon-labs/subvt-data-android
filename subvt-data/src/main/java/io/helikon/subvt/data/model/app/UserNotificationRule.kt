package io.helikon.subvt.data.model.app

/**
 * Used when creating a new notification rule for the user.
 */
data class CreateUserNotificationRuleRequest(
    val notificationTypeCode: String,
    val name: String?,
    val networkId: Long?,
    val isForAllValidators: Boolean,
    val userValidatorIds: Set<Long>,
    val periodType: NotificationPeriodType,
    val period: Int,
    val userNotificationChannelIds: Set<Long>,
    val parameters: List<NewUserNotificationRuleParameter>,
    val notes: String?,
)

/**
 * Actual parameter for a rule - returned by the GET rule service.
 */
data class UserNotificationRuleParameter(
    val userNotificationRuleId: Long,
    val parameterTypeId: Long,
    val parameterTypeCode: String,
    val order: Short,
    val value: String,
)

/**
 * Used in the request to create a new notification rule for the user.
 */
data class NewUserNotificationRuleParameter(
    val parameterTypeId: Long,
    val value: String,
)

/**
 * Returned by the GETter service for the user's notification rules.
 */
data class UserNotificationRule(
    val id: Long,
    val userId: Long,
    val notificationType: NotificationType,
    val name: String?,
    val network: Network?,
    val isForAllValidators: Boolean,
    val periodType: NotificationPeriodType,
    val period: Int,
    val validators: List<UserValidator>,
    val notificationChannels: List<UserNotificationChannel>,
    val parameters: List<UserNotificationRuleParameter>,
    val notes: String?,
)