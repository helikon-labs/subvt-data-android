package io.helikon.subvt.data.hash

import org.spongycastle.jcajce.provider.digest.Blake2b

internal object Hasher {
    private val blake2bLock = Any()
    private val blake2b256 = Blake2b.Blake2b256()
    private val blake2b512 = Blake2b.Blake2b512()

    fun ByteArray.blake2b256(): ByteArray = withBlake2bLock { blake2b256.digest(this) }
    fun ByteArray.blake2b512(): ByteArray = withBlake2bLock { blake2b512.digest(this) }

    private inline fun <T> withBlake2bLock(action: () -> T) = synchronized(blake2bLock, action)
}