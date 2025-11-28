import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import violett.pro.cvchat.domain.model.Contact

@Composable
fun ContactItem(
    modifier: Modifier = Modifier,
    contact: Contact,
    onClick: () -> Unit,
    onRenameConfirmed: (String) -> Unit = {},
    onPinClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var menuExpanded by remember { mutableStateOf(false) }
    var isRenameActive by remember { mutableStateOf(false) }

    val displayName = contact.name.ifEmpty { contact.tempId }

    var renameTextFieldValue by remember {
        mutableStateOf(TextFieldValue(text = displayName))
    }

    fun saveName() {
        isRenameActive = false
        onRenameConfirmed(renameTextFieldValue.text)
        keyboardController?.hide()
    }

    LaunchedEffect(isRenameActive) {
        if (isRenameActive) {
            renameTextFieldValue = TextFieldValue(
                text = displayName,
                selection = TextRange(displayName.length)
            )
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    val gradientBrush = Brush.linearGradient(
        colorStops = arrayOf(
            0.00f to MaterialTheme.colorScheme.surfaceContainerLow.copy(.7f),
            5.75f to MaterialTheme.colorScheme.surface,
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(72.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.surfaceDim.copy(0.7f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = null,
                indication = null
            ) {
                if (!isRenameActive) {
                    onClick()
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (!isRenameActive) {
            Text(
                text = displayName,
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        } else {
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = renameTextFieldValue,
                onValueChange = { renameTextFieldValue = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { saveName() }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }

        Box(
            modifier = Modifier.padding(end = 4.dp)
        ) {
            if (isRenameActive) {
                IconButton(onClick = { saveName() }) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Сохранить",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                IconButton(
                    onClick = { menuExpanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Действия",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Column {
                    AnimatedVisibility(
                        visible = menuExpanded,
                        enter = fadeIn(animationSpec = tween(200)) +
                                expandVertically(
                                    animationSpec = tween(200),
                                    expandFrom = Alignment.Top
                                ),
                        exit = fadeOut(animationSpec = tween(150)) +
                                shrinkVertically(
                                    animationSpec = tween(150),
                                    shrinkTowards = Alignment.Top
                                )
                    ) {
                        DropdownMenu(
                            expanded = true,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Изменить имя") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.Edit, contentDescription = null)
                                },
                                onClick = {
                                    menuExpanded = false
                                    isRenameActive = true
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Закрепить") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.PushPin, contentDescription = null)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onPinClick()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Удалить",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}