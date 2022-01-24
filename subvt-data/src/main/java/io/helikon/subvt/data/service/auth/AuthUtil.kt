package io.helikon.subvt.data.service.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.web3j.crypto.ECKeyPair
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

const val commsKeyAlias = "SubVTCommsKey"

internal fun ByteArray.toHex(): String = joinToString(separator = "") { byte ->
    "%02x".format(byte)
}

internal fun generateEncryptionKeyPair() {
    val notBefore = Calendar.getInstance()
    val notAfter = Calendar.getInstance()
    notAfter.add(Calendar.YEAR, 1)
    val spec = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_RSA,
        "AndroidKeyStore"
    )
    spec.initialize(
        KeyGenParameterSpec.Builder(
            commsKeyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .setKeySize(2048)
            .setKeyValidityStart(notBefore.time)
            .setKeyValidityEnd(notAfter.time)
            .setCertificateSubject(X500Principal("CN=subvt"))
            .setCertificateSerialNumber(BigInteger(System.currentTimeMillis().toString()))
            .build()
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

internal fun storeKeyPair(context: Context, keyPair: ECKeyPair) {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    if (!keyStore.containsAlias(commsKeyAlias)) {
        generateEncryptionKeyPair()
    }
    val entry = keyStore.getEntry(commsKeyAlias, null) as KeyStore.PrivateKeyEntry
    val inCipher = Cipher.getInstance(
        "RSA/ECB/PKCS1Padding",
        "AndroidKeyStoreBCWorkaround"
    )
    inCipher.init(Cipher.ENCRYPT_MODE, entry.certificate.publicKey)
    var cipherOutputStream = CipherOutputStream(
        FileOutputStream(getPrivateKeyEncryptedFilePath(context)),
        inCipher
    )
    cipherOutputStream.write(keyPair.privateKey.toByteArray())
    cipherOutputStream.close()
    cipherOutputStream = CipherOutputStream(
        FileOutputStream(getPublicKeyEncryptedFilePath(context)),
        inCipher
    )
    cipherOutputStream.write(keyPair.publicKey.toByteArray())
    cipherOutputStream.close()
}

private fun getFileBytes(path: String, outCipher: Cipher): ByteArray {
    val cipherInputStream = CipherInputStream(
        FileInputStream(path),
        outCipher
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
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    val entry = keyStore.getEntry(commsKeyAlias, null) as KeyStore.PrivateKeyEntry
    val outCipher1 = Cipher.getInstance(
        "RSA/ECB/PKCS1Padding",
        "AndroidKeyStoreBCWorkaround"
    )
    outCipher1.init(Cipher.DECRYPT_MODE, entry.privateKey)
    val privateKeyBytes = getFileBytes(getPrivateKeyEncryptedFilePath(context), outCipher1)
    val outCipher2 = Cipher.getInstance(
        "RSA/ECB/PKCS1Padding",
        "AndroidKeyStoreBCWorkaround"
    )
    outCipher2.init(Cipher.DECRYPT_MODE, entry.privateKey)
    val publicKeyBytes = getFileBytes(getPublicKeyEncryptedFilePath(context), outCipher2)
    return ECKeyPair(BigInteger(privateKeyBytes), BigInteger(publicKeyBytes))
}