package com.example.myapplication

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.ECGenParameterSpec
import java.util.Arrays
import java.util.Date
import java.util.stream.Collectors
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

val TS_KEYSTORE_ALIAS = "Tinysweeper-cert-key"
val TS_DEFAULT_KEYSTORE = "AndroidKeyStore"
val EC_CURVE = "secp256r1"

// Keystore methods are all final; preventing Mockito from mocking them.
open class KeyStoreWrapper(
  private var keyStore: KeyStore
) {
  open fun load(param: KeyStore.LoadStoreParameter?) {
    keyStore.load(param)
  }

  open fun getCertificateChain(alias: String): Array<Certificate> = keyStore.getCertificateChain(alias)
}

class AndroidKeyAttestor(
  private var keyStoreWrapped: KeyStoreWrapper,
  private var keyPairGenerator: KeyPairGenerator,
  private var ctx: Context?,
) {
  init {
    keyStoreWrapped.load(null)
  }

  fun debugWriteKeychain() {
    var certs = keyStoreWrapped.getCertificateChain(TS_KEYSTORE_ALIAS)
    val path = ctx!!.getExternalFilesDir(null)

    val file = File(path, "keychain.der")
    FileOutputStream(file).use {
      for (cert in certs) {
        it.write(cert.toString().toByteArray())
      }
    }
  }

  fun generateKeyPair(spec: AlgorithmParameterSpec) {
    keyPairGenerator.initialize(spec)
    keyPairGenerator.generateKeyPair()
  }

  fun getKeyPairAlgorithmParameterSpec(challenge: ByteArray): KeyGenParameterSpec {
    val builder =
      KeyGenParameterSpec
        // PURPOSE_ATTEST_KEY doesn't exist on older devices
        .Builder(TS_KEYSTORE_ALIAS, KeyProperties.PURPOSE_SIGN)
        .setAlgorithmParameterSpec(ECGenParameterSpec(EC_CURVE))
        .setDigests(KeyProperties.DIGEST_SHA256)
        .setAttestationChallenge(challenge)

    return builder.build()
  }
}
