package violett.pro.cvchat.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

import androidx.navigation3.ui.NavDisplay
import violett.pro.cvchat.ui.contacts.ContactScreen
import violett.pro.cvchat.ui.keygen.KeyGenScreen

//private fun NavBackStack<NavKey>.beSmart() {
//
//    // Remove any existing detail routes, then add the new detail route
//    removeIf { it is TestScreenUi }
//    add(TestScreenUi) // or another
//}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NavRoot(
    modifier: Modifier = Modifier,
    startScreen: NavKey,
    onKeysGenerated: (tempId: String) -> Unit,
    tempId: String?,
) {
    val backStack = rememberNavBackStack(startScreen)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        sceneStrategy = listDetailStrategy,
        entryProvider = { key ->
            when (key) {

                is KeyGenScreenUi -> {
                    NavEntry(key=key){
                        KeyGenScreen(
                            modifier = Modifier.fillMaxSize(),
                            onKeysGenerated = {
                                backStack.add(ContactScreenUi)
                                backStack.remove(KeyGenScreenUi)
                                onKeysGenerated(it)
                            }
                        )
                    }
                }

                is ContactScreenUi -> {
                    NavEntry(key=key){
                        ContactScreen(
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                else -> throw RuntimeException("Invalid NavKey: $key")
            }
        }
    )
}