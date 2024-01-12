package io.helikon.subvt.data.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.helikon.subvt.data.exception.APIException
import io.helikon.subvt.data.model.APIError
import retrofit2.Response

private val gson: Gson =
    GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

internal inline fun <reified T> extractResponse(response: Response<T>): Result<T?> {
    return if (response.isSuccessful) {
        Result.success(response.body())
    } else {
        val errorBody = response.errorBody()?.string()
        val error =
            if (errorBody != null) {
                try {
                    gson.fromJson(errorBody, APIError::class.java)
                } catch (e: Exception) {
                    APIError(errorBody)
                }
            } else {
                APIError("No description.")
            }
        Result.failure(APIException(response.code(), error))
    }
}
