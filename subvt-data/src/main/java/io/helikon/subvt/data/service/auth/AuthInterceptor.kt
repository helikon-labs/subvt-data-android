package io.helikon.subvt.data.service.auth

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.toHexString
import okio.Buffer
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.ec.CustomNamedCurves
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.io.IOException
import java.math.BigInteger
import java.security.*

internal class AuthInterceptor(context: Context): Interceptor {
    private val keyPair: ECKeyPair
    private val signer = Signer()

    init {
        val storedKeyPair = getKeyPair(context)
        if (storedKeyPair == null) {
            keyPair = Keys.createEcKeyPair()
            storeKeyPair(context, keyPair)
        } else {
            keyPair = storedKeyPair
        }
        // keyPair = Keys.createEcKeyPair()
        signer.init(keyPair.privateKey)
    }

    private fun getRequestBody(request: Request): String {
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        return buffer.readUtf8()
    }

    private fun compressPubKey(pubKey: BigInteger): String {
        val pubKeyYPrefix = when (pubKey.testBit(0)) {
            true -> "03"
            false -> "02"
        }
        val pubKeyHex = pubKey.toString(16)
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
            val compressedPubKey = compressPubKey(keyPair.publicKey)
            request = request.newBuilder()
                .header("SubVT-Public-Key", compressedPubKey)
                .header("SubVT-Nonce", nonce.toString())
                .header("SubVT-Signature", signatureDER)
                .build()
        }
        Log.d("SUBVT", "${request.headers}")
        return chain.proceed(request)
    }
}