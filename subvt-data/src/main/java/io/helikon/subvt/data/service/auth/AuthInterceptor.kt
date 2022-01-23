package io.helikon.subvt.data.service.auth

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi
import org.spongycastle.jce.interfaces.ECPublicKey
import org.spongycastle.jce.provider.BouncyCastleProvider
import java.io.IOException
import java.security.*
import java.security.spec.ECGenParameterSpec

internal class AuthInterceptor(context: Context): Interceptor {

    private val signer: Signature
    private val keyPair: KeyPair

    init {
        Security.addProvider(BouncyCastleProvider())
        signer = Signature.getInstance("SHA256withECDSA", "SC")
        val storedKeyPair = getKeyPair(context)
        if (storedKeyPair == null) {
            val generator = KeyPairGeneratorSpi.getInstance("EC", "SC")
            val spec = ECGenParameterSpec("secp256k1")
            generator.initialize(spec, SecureRandom())
            keyPair = generator.genKeyPair()
            storeKeyPair(context, keyPair)
        } else {
            keyPair = storedKeyPair
        }
        signer.initSign(keyPair.private)
    }

    private fun getRequestBody(request: Request): String {
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        return buffer.readUtf8()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val path = request.url.encodedPath
        if (path.startsWith("/secure")) {
            val method = request.method
            val nonce = System.currentTimeMillis()
            val body = getRequestBody(request)
            signer.update((method + path + body + nonce).toByteArray())
            val signature = signer.sign()
            val publicKey = keyPair.public as ECPublicKey
            request = request.newBuilder()
                .header("SubVT-Public-Key", publicKey.q.getEncoded(true).toHex())
                .header("SubVT-Nonce", nonce.toString())
                .header("SubVT-Signature", signature.toHex())
                .build()
        }
        Log.d("SUBVT", "${request.headers}")
        return chain.proceed(request)
    }
}