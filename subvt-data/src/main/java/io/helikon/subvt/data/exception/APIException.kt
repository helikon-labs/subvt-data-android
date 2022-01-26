package io.helikon.subvt.data.exception

import io.helikon.subvt.data.model.APIError

fun <T> Result<T>.apiException(): APIException? =
    if (isSuccess) {
        null
    } else {
        exceptionOrNull() as? APIException
    }

data class APIException(val code: Int, val error: APIError) : Exception()