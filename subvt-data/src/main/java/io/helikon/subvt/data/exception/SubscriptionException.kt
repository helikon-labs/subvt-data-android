package io.helikon.subvt.data.exception

/**
 * Exception thrown by the RPC subscription services.
 * Contains a descriptive message.
 */
class SubscriptionException(message: String): Exception(message)