package violett.pro.cvchat.ui.test

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun TestScreen(
    modifier: Modifier = Modifier,
    viewModel: TestViewModel = koinViewModel(),
    onNavigateToDetails: () -> Unit
) {
    val state by viewModel.testState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // LaunchedEffect(Unit) запускается 1 раз при входе на экран.
    // Если экран перевернуть - он перезапустится (но Channel буферизирован, так что ок).
    // Если просто рекомпозиция (перерисовка) - он НЕ перезапустится.
    LaunchedEffect(Unit) {
        viewModel.action.collect { action ->
            when (action) {
                is TestAction.NavigateToDetails -> {
                    onNavigateToDetails()
                }
                is TestAction.ShowToast -> {
                    Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.textData)
                Button(onClick = { viewModel.testFunc(false) }) {
                    Text("GOTO")
                }

            }
        }
    }
}