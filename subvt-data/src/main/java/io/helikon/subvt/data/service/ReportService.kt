package io.helikon.subvt.data.service

/**
 * Public interface for the report service.
 * See ReportServiceInternal Retrofit interface for the actual interface.
 */
class ReportService(baseURL: String) {
    private val service = ReportServiceInternal.getInstance(baseURL)

    suspend fun getEraReport(
        startEraIndex: Int,
        endEraIndex: Int?,
    ) = extractResponse(
        service.getEraReport(
            startEraIndex,
            endEraIndex,
        ),
    )

    suspend fun getEraValidatorReport(
        validatorAccountIdHex: String,
        startEraIndex: Int,
        endEraIndex: Int?,
    ) = extractResponse(
        service.getEraValidatorReport(
            validatorAccountIdHex,
            startEraIndex,
            endEraIndex,
        ),
    )

    suspend fun getValidatorDetails(validatorAccountIdHex: String) =
        extractResponse(
            service.getValidatorDetails(
                validatorAccountIdHex,
            ),
        )

    suspend fun getValidatorSummary(validatorAccountIdHex: String) =
        extractResponse(
            service.getValidatorSummary(
                validatorAccountIdHex,
            ),
        )

    suspend fun getValidatorList() =
        extractResponse(
            service.getValidatorList(),
        )

    suspend fun getActiveValidatorList() =
        extractResponse(
            service.getActiveValidatorList(),
        )

    suspend fun getInactiveValidatorList() =
        extractResponse(
            service.getInactiveValidatorList(),
        )

    suspend fun searchValidators(query: String) =
        extractResponse(
            service.searchValidators(query),
        )

    suspend fun getOneKVNominatorSummaries() =
        extractResponse(
            service.getOneKVNominatorSummaries(),
        )

    suspend fun getAllEras() =
        extractResponse(
            service.getAllEras(),
        )

    suspend fun getCurrentEra() =
        extractResponse(
            service.getCurrentEra(),
        )

    suspend fun getValidatorEraRewardReport(validatorAccountIdHex: String) =
        extractResponse(
            service.getValidatorEraRewardReport(
                validatorAccountIdHex,
            ),
        )

    suspend fun getValidatorEraPayoutReport(validatorAccountIdHex: String) =
        extractResponse(
            service.getValidatorEraPayoutReport(
                validatorAccountIdHex,
            ),
        )

    suspend fun getSessionValidatorReport(
        validatorAccountIdHex: String,
        startSessionIndex: Int,
        endSessionIndex: Int?,
    ) = extractResponse(
        service.getSessionValidatorReport(
            validatorAccountIdHex,
            startSessionIndex,
            endSessionIndex,
        ),
    )

    suspend fun getCurrentSession() =
        extractResponse(
            service.getCurrentSession(),
        )
}
