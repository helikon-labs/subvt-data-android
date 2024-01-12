package io.helikon.subvt.data

import android.content.Context
import io.helikon.subvt.data.service.auth.clearKeys

/**
 * Top-level class. Only used for the key reset at the moment.
 */
class SubVTData {
    companion object {
        /**
         * Clear keys.
         */
        fun reset(context: Context) {
            clearKeys(context)
        }
    }
}
