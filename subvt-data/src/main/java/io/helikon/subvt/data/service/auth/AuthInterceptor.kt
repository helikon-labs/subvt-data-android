package io.helikon.subvt.data.service.auth

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.spongycastle.jce.interfaces.ECPublicKey
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Hash
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
import java.io.IOException
import java.math.BigInteger
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
            val generator = KeyPairGenerator.getInstance("ECDSA", "SC")
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
        //val pair = Keys.createEcKeyPair()
        //Log.d("SUBVT", "Priv key: ${pair.privateKey.toString(16)}")
        //Log.d("SUBVT", "Public key: ${pair.publicKey.toString(16)}")

        var request = chain.request()
        val path = request.url.encodedPath
        if (path.startsWith("/secure")) {
            val method = request.method
            val nonce = System.currentTimeMillis()
            val body = getRequestBody(request)
            Log.d("SUBVT", "TO SIGN [${(method + path + body + nonce)}]")

            val message = method + path + body + nonce
            val pair = Keys.createEcKeyPair()
            Log.d("SUBVT", "PUB KEY ${pair.publicKey.toByteArray().toHex()}")
            val hash_bytes = Hash.sha3(message.toByteArray())
            val signature1 = Sign.signMessage(hash_bytes, pair)
            Log.d("SUBVT", "PUB KEY COMP :: ${compressPubKey(pair.publicKey)}")
            Log.d("SUBVT", "SIGN R :: ${signature1.r.toHex()}")
            Log.d("SUBVT", "SIGN S :: ${signature1.s.toHex()}")
            val signature2 = Sign.signMessage(hash_bytes, pair)
            Log.d("SUBVT", "SIGN R :: ${signature2.r.toHex()}")
            Log.d("SUBVT", "SIGN S :: ${signature2.s.toHex()}")

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