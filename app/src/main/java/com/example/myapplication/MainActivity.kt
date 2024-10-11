package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore


class MainActivity : ComponentActivity() {
  fun keyattest() {
    val aka = AndroidKeyAttestor(
      ctx = getApplicationContext(),keyStoreWrapped = KeyStoreWrapper(KeyStore.getInstance(TS_DEFAULT_KEYSTORE)),
      keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, TS_DEFAULT_KEYSTORE))

    val challenge = "aaaabbbbccccddddeeeeffffgggghhhh".toByteArray()
    val spec = aka.getKeyPairAlgorithmParameterSpec(challenge)

    aka.generateKeyPair(spec)
    aka.debugWriteKeychain()

  }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    keyattest()

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Greeting(
            name = "Android",
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun Greeting(
  name: String,
  modifier: Modifier = Modifier
) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme {
    Greeting("Android")
  }
}
