package io.helikon.subvt.data.model.substrate

import com.google.gson.annotations.SerializedName

enum class ParaAssignmentKind {
    @SerializedName("Parathread")
    PARATHREAD,

    @SerializedName("Parachain")
    PARACHAIN,
}

/**
 * Parachain or paravalidator assignment.
 */
data class ParaCoreAssignment(
    val coreIndex: Int,
    val paraId: Int,
    val paraAssignmentKind: ParaAssignmentKind,
    val groupIndex: Int,
)