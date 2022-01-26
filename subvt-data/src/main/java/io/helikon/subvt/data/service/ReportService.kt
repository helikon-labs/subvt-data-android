package io.helikon.subvt.data.service

/**
 * Public interface for the report service.
 * See ReportServiceInternal Retrofit interface for the actual interface.
 */
class ReportService(baseURL: String) {
    private val service = ReportServiceInternal.getInstance(baseURL)

    suspend fun getEraReport(
        startEraIndex: Int,
        endEraIndex: Int?
    ) =
        extractResponse(
            service.getEraReport(
                startEraIndex,
                endEraIndex
            )
        )

    suspend fun getEraValidatorReport(
        validatorAccountIdHex: String,
        startEraIndex: Int,
        endEraIndex: Int?
    ) =
        extractResponse(
            service.getEraValidatorReport(
                validatorAccountIdHex,
                startEraIndex,
                endEraIndex
            )
        )
}