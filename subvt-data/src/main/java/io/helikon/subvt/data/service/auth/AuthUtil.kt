/**
 * Authentication-related utility functions.
 */
package io.helikon.subvt.data.service.auth

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import okhttp3.internal.toHexString
import org.web3j.crypto.ECKeyPair
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger

internal fun Int.toPaddedHexString(): String {
    return toHexString().run {
        if (length % 2 != 0) {
            "0$this"
        } else {
            this
        }
    }
}

internal fun BigInteger.toPaddedHexString(): String {
    return toString(16).run {
        if (length % 2 != 0) {
            "0$this"
        } else {
            this
        }
    }
}

internal fun clearKeys(context: Context) {
    File(getPrivateKeyEncryptedFilePath(context)).delete()
    File(getPublicKeyEncryptedFilePath(context)).delete()
}

private fun getMasterKeyAlias(): String {
    return MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
}

private fun getPrivateKeyEncryptedFilePath(context: Context): String {
    val filesDir = context.filesDir.absolutePath
    return filesDir + File.separator + "subvt_comms_prv"
}

private fun getPublicKeyEncryptedFilePath(context: Context): String {
    val filesDir = context.filesDir.absolutePath
    return filesDir + File.separator + "subvt_comms_pub"
}

internal fun storeKeyPair(
    context: Context,
    keyPair: ECKeyPair,
) {
    val privateKeyFile =
        EncryptedFile.Builder(
            File(getPrivateKeyEncryptedFilePath(context)),
            context,
            getMasterKeyAlias(),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()
    privateKeyFile
        .openFileOutput()
        .run {
            write(keyPair.privateKey.toByteArray())
            flush()
            close()
        }

    val publicKeyFile =
        EncryptedFile.Builder(
            File(getPublicKeyEncryptedFilePath(context)),
            context,
            getMasterKeyAlias(),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()
    publicKeyFile
        .openFileOutput()
        .run {
            write(keyPair.publicKey.toByteArray())
            flush()
            close()
        }
}

private fun getEncryptedFileBytes(
    context: Context,
    path: String,
): ByteArray {
    val encryptedFile =
        EncryptedFile.Builder(
            File(path),
            context,
            getMasterKeyAlias(),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
        ).build()
    val byteArrayOutputStream = ByteArrayOutputStream()
    encryptedFile.openFileInput().run {
        var nextByte: Int = read()
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte)
            nextByte = read()
        }
        close()
    }
    return byteArrayOutputStream.toByteArray()
}

internal fun getKeyPair(context: Context): ECKeyPair? {
    if (!File(getPrivateKeyEncryptedFilePath(context)).exists()) {
        return null
    }
    val privateKeyBytes = getEncryptedFileBytes(context, getPrivateKeyEncryptedFilePath(context))
    val publicKeyBytes = getEncryptedFileBytes(context, getPublicKeyEncryptedFilePath(context))
    return ECKeyPair(BigInteger(privateKeyBytes), BigInteger(publicKeyBytes))
}
