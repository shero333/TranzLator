package com.risibleapps.translator

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest

class ClassOne {

    companion object {

        fun getApplicationSignature(context: Context, packageName: String = context.packageName): /*List<String>*/String {
            val signatureList: /*List<String>*/String
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // New signature
                    val sig = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
                    signatureList = if (sig.hasMultipleSigners()) ({
                        // Send all with apkContentsSigners
                        sig.apkContentsSigners.map {
                            val digest = MessageDigest.getInstance("SHA1")
                            digest.update(it.toByteArray())
                            bytesToHex(digest.digest())
                        }
                    }).toString() else ({
                        // Send one with signingCertificateHistory
                        sig.signingCertificateHistory.map {
                            val digest = MessageDigest.getInstance("SHA1")
                            digest.update(it.toByteArray())
                            bytesToHex(digest.digest())
                        }
                    }).toString()
                } else {
                    val sig = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
                    signatureList = sig.map {
                        val digest = MessageDigest.getInstance("SHA1")
                        digest.update(it.toByteArray())
                        bytesToHex(digest.digest())
                    }.toString()
                }

                return signatureList
            } catch (e: Exception) {
                // Handle error
            }
            return /*emptyList()*/"nothing"
        }



        fun bytesToHex(bytes: ByteArray): String {
            val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
            val hexChars = CharArray(bytes.size * 2)
            var v: Int
            for (j in bytes.indices) {
                v = bytes[j].toInt() and 0xFF
                hexChars[j * 2] = hexArray[v.ushr(4)]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
            }
            return String(hexChars)
        }

    }
}