package io.helikon.subvt.data.service.auth

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.io.IOException
import java.math.BigInteger

/**
 * OkHTTP authentication interceptor.
 * Adds public key, nonce and ECDSA signature headers where necessary.
 */
internal class AuthInterceptor(context: Context) : Interceptor {
    private val keyPair: ECKeyPair
    private val signer: Signer

    init {
        val storedKeyPair = getKeyPair(context)
        if (storedKeyPair == null) {
            keyPair = Keys.createEcKeyPair()
            storeKeyPair(context, keyPair)
        } else {
            keyPair = storedKeyPair
        }
        signer = Signer(keyPair.privateKey)
    }

    private fun getRequestBody(request: Request): String {
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        return buffer.readUtf8()
    }

    private fun compressPublicKey(pubKey: BigInteger): String {
        val pubKeyYPrefix =
            when (pubKey.testBit(0)) {
                true -> "03"
                false -> "02"
            }
        val pubKeyHex = pubKey.toPaddedHexString()
        val pubKeyX = pubKeyHex.substring(0, 64)
        return pubKeyYPrefix + pubKeyX
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val path = request.url.encodedPath
        if (path.startsWith("/secure")) {
            val method = request.method
            val nonce = System.currentTimeMillis()
            val body = getRequestBody(request)
            val signatureDER = signer.sign(method + path + body + nonce)
            val compressedPublicKey = compressPublicKey(keyPair.publicKey)
            request =
                request.newBuilder()
                    .header("SubVT-Public-Key", compressedPublicKey)
                    .header("SubVT-Nonce", nonce.toString())
                    .header("SubVT-Signature", signatureDER)
                    .build()
        }
        return chain.proceed(request)
    }
}
