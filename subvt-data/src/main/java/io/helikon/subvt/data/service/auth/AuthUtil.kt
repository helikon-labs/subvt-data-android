/**
 * Authentication-related utility functions.
 */
package io.helikon.subvt.data.service.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import okhttp3.internal.toHexString
import org.web3j.crypto.ECKeyPair
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.security.auth.x500.X500Principal

const val COMMS_KEY_ALIAS = "SubVTCommsKey"

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

internal fun generateEncryptionKeyPair() {
    val notBefore = Calendar.getInstance()
    val notAfter = Calendar.getInstance()
    notAfter.add(Calendar.YEAR, 1)
    val spec =
        KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA,
            "AndroidKeyStore",
        )
    spec.initialize(
        KeyGenParameterSpec.Builder(
            COMMS_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .setKeySize(2048)
            .setKeyValidityStart(notBefore.time)
            .setKeyValidityEnd(notAfter.time)
            .setCertificateSubject(X500Principal("CN=subvt"))
            .setCertificateSerialNumber(BigInteger(System.currentTimeMillis().toString()))
            .build(),
    )
    spec.generateKeyPair()
}

private fun getPrivateKeyEncryptedFilePath(context: Context): String {
    val filesDir = context.filesDir.absolutePath
    return filesDir + File.separator + "subvt_comms_prv"
}

private fun getPublicKeyEncryptedFilePath(context: Context): String {
    val filesDir = context.filesDir.absolutePath
    return filesDir + File.separator + "subvt_comms_pub"
}

private fun getCipherInstance() =
    Cipher.getInstance(
        "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
    )

internal fun storeKeyPair(
    context: Context,
    keyPair: ECKeyPair,
) {
    /*
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    if (!keyStore.containsAlias(COMMS_KEY_ALIAS)) {
        generateEncryptionKeyPair()
    }
    val entry = keyStore.getEntry(COMMS_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
    val inCipher = getCipherInstance()
    inCipher.init(Cipher.ENCRYPT_MODE, entry.certificate.publicKey)
    var cipherOutputStream =
        CipherOutputStream(
            FileOutputStream(getPrivateKeyEncryptedFilePath(context)),
            inCipher,
        )
    cipherOutputStream.write(keyPair.privateKey.toByteArray())
    cipherOutputStream.close()
    cipherOutputStream =
        CipherOutputStream(
            FileOutputStream(getPublicKeyEncryptedFilePath(context)),
            inCipher,
        )
    cipherOutputStream.write(keyPair.publicKey.toByteArray())
    cipherOutputStream.close()
     */
    val privateKeyStream = FileOutputStream(getPrivateKeyEncryptedFilePath(context))
    privateKeyStream.write(keyPair.privateKey.toByteArray())
    privateKeyStream.flush()
    privateKeyStream.close()
    val publicKeyStream = FileOutputStream(getPublicKeyEncryptedFilePath(context))
    publicKeyStream.write(keyPair.publicKey.toByteArray())
    publicKeyStream.flush()
    publicKeyStream.close()
}

private fun getFileBytes(path: String): ByteArray {
    val inputStream = FileInputStream(path)
    val byteList = mutableListOf<Byte>()
    while (true) {
        val nextByte = inputStream.read().toByte()
        if (nextByte == (-1).toByte()) {
            break
        }
        byteList.add(nextByte)
    }
    return byteList.toByteArray()
}

private fun getEncryptedFileBytes(
    path: String,
    outCipher: Cipher,
): ByteArray {
    val cipherInputStream =
        CipherInputStream(
            FileInputStream(path),
            outCipher,
        )
    val byteList = mutableListOf<Byte>()
    while (true) {
        val nextByte = cipherInputStream.read().toByte()
        if (nextByte == (-1).toByte()) {
            break
        }
        byteList.add(nextByte)
    }
    return byteList.toByteArray()
}

internal fun getKeyPair(context: Context): ECKeyPair? {
    if (!File(getPrivateKeyEncryptedFilePath(context)).exists()) {
        return null
    }
    /*
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    val entry = keyStore.getEntry(COMMS_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
    val outCipher1 = getCipherInstance()
    outCipher1.init(Cipher.DECRYPT_MODE, entry.privateKey)
    val privateKeyBytes = getEncryptedFileBytes(getPrivateKeyEncryptedFilePath(context), outCipher1)
    val outCipher2 = getCipherInstance()
    outCipher2.init(Cipher.DECRYPT_MODE, entry.privateKey)
    val publicKeyBytes = getEncryptedFileBytes(getPublicKeyEncryptedFilePath(context), outCipher2)
    return ECKeyPair(BigInteger(privateKeyBytes), BigInteger(publicKeyBytes))
     */
    val privateKeyBytes = getFileBytes(getPrivateKeyEncryptedFilePath(context))
    val publicKeyBytes = getFileBytes(getPublicKeyEncryptedFilePath(context))
    return ECKeyPair(BigInteger(privateKeyBytes), BigInteger(publicKeyBytes))
}
