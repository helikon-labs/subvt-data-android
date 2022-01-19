package io.helikon.subvt.data.encrypt.json

internal fun ByteArray.copyBytes(from: Int, size: Int) = copyOfRange(from, from + size)