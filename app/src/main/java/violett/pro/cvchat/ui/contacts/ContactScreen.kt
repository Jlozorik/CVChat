package violett.pro.cvchat.ui.contacts

import ContactItem
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CoPresent
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import violett.pro.cvchat.domain.model.Contact

@Composable
fun ContactScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactViewModel = koinViewModel(),
    lazyColumnState : LazyListState,
    onChatClick: (Contact) -> Unit = {},
) {
    val state by viewModel.contactState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.action.collect { action ->
            when(action) {
                is ContactActions.NavigateToChat -> {
                    onChatClick(action.contact)
                }
                is ContactActions.ShowToast -> {
                    Toast.makeText(context, action.message, Toast.LENGTH_SHORT).show()
                }

                is ContactActions.RefreshContact -> {
                    viewModel.loadContacts()
                }
            }

        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            if (state.contacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = {
                            showDialog = true
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(.8f)
                            .padding(bottom = 30.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(
                            text = "Добавить контакт",
                            fontSize = 16.sp
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {

                    Icon(
                        modifier = Modifier.size(128.dp),
                        imageVector = Icons.Outlined.CoPresent,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "У вас еще нет контактов",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )


                }


            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top=20.dp),
                    state = lazyColumnState,
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    items(state.contacts) {contact ->
                        ContactItem(
                            contact = contact,
                            onClick = {
                                onChatClick(contact)
                            },
                            onRenameConfirmed = {
                                viewModel.changeName(contact.tempId,it)
                            },
                            onPinClick = {

                            },
                            onDeleteClick = {
                                viewModel.deleteContact(contact)
                            },
                        )
                    }


                }
            }

        }
        if (showDialog) {
            CustomInputDialog(
                onDismiss = { showDialog = false },
                onConfirm = { input ->
                    Log.d("ContactScreen", "ContactScreen: $input")
                    scope.launch {
                        viewModel.addUser(input)
                    }
                    showDialog = false
                }
            )
        }
    }

}