package io.helikon.subvt.data.service.auth

import okhttp3.internal.toHexString
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.crypto.ec.CustomNamedCurves
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.crypto.params.ECPrivateKeyParameters
import org.spongycastle.crypto.signers.ECDSASigner
import org.spongycastle.crypto.signers.HMacDSAKCalculator
import java.math.BigInteger
import java.security.MessageDigest

internal class Signer {
    private val signer: ECDSASigner = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
    private val halfCurveOrder: BigInteger
    private val curve: ECDomainParameters
    private val messageDigest = MessageDigest.getInstance("SHA-256")

    init {
        val curveParams = CustomNamedCurves.getByName("secp256k1")
        halfCurveOrder = curveParams.n.shiftRight(1)
        curve = ECDomainParameters(
            curveParams.curve,
            curveParams.g,
            curveParams.n,
            curveParams.h
        )
    }

    fun init(privateKey: BigInteger) {
        signer.init(
            true,
            ECPrivateKeyParameters(privateKey, curve)
        )
    }

    fun sign(message: String): String {
        val hash = messageDigest.digest(message.toByteArray())
        val signature = signer.generateSignature(hash)
        val r = signature[0]
        var s = signature[1]
        if (s > halfCurveOrder) {
            s = curve.n.subtract(s)
        }
        var rHex = r.toString(16)
        if (rHex[0].digitToInt(16) >= 8) {
            rHex = "00$rHex"
        }
        val rLen = Integer.toHexString(rHex.length / 2)
        var sHex = s.toString(16)
        if (sHex[0].digitToInt(16) >= 8) {
            sHex = "00$sHex"
        }
        val sLen = Integer.toHexString(sHex.length / 2)
        val len = (rHex.length / 2 + sHex.length / 2 + 4).toHexString()
        return "30${len}02${rLen}${rHex}02${sLen}${sHex}"
    }
}