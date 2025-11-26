package violett.pro.cvchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import violett.pro.cvchat.ui.navigation.ContactScreenUi
import violett.pro.cvchat.ui.navigation.KeyGenScreenUi
import violett.pro.cvchat.ui.navigation.NavRoot
import violett.pro.cvchat.ui.theme.CVChatTheme

class MainActivity : ComponentActivity() {

    private val sharedPrefs by lazy {
        getSharedPreferences("cvchat_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val tempId = sharedPrefs.getString("TEMP_ID", null)
        val areKeysGenerated = sharedPrefs.getBoolean("KEY_GENERATED_FLAG", false)
        val startScreen = if (areKeysGenerated) {
            ContactScreenUi
        } else {
            KeyGenScreenUi
        }
        setContent {
            CVChatTheme {
                LaunchedEffect(true) {
                    println(tempId)
                }
                val onKeysGenerated = { tempId: String ->
                    sharedPrefs.edit {
                        putString("TEMP_ID", tempId)
                        putBoolean("KEY_GENERATED_FLAG", true)
                    }
                    Log.d("MainActivity", "onKeysGenerated: $tempId")
                }
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavRoot(
                        modifier = Modifier.fillMaxSize(),
                        startScreen = startScreen,
                        tempId = tempId,
                        onKeysGenerated = {
                            onKeysGenerated(it)
                        }
                    )
                }
            }
        }
    }
}

