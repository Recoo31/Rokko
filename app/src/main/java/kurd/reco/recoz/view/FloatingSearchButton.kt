package kurd.reco.recoz.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun FloatingSearchButton(
    icon: ImageVector = Icons.Default.Search,
    enabled: Boolean = true,
    clearIcon: ImageVector = Icons.Default.Clear,
    closeIcon: ImageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    overrideOnClick: Boolean = false,
    onTextChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var opened by remember { mutableStateOf(false) }
    val textState = remember { mutableStateOf("") }
    val enabledColor = if (enabled) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    val animatedColor by animateColorAsState(
        targetValue = enabledColor,
        animationSpec = tween(), label = "animatedColor"
    )

    Surface(
        color = animatedColor,
        modifier = modifier,
        shape = FloatingActionButtonDefaults.shape,
        shadowElevation = 6.dp,
        onClick = {
            if (overrideOnClick) {
                onClick()
            } else {
                if (!opened) {
                    opened = true
                } else {
                    focusRequester.requestFocus()
                }
            }
        },
        enabled = enabled
    ) {
        AnimatedContent(targetState = opened, label = "") { expand ->
            if (expand) {
                SearchBar(
                    close = {
                        opened = false
                    },
                    clearIcon = clearIcon,
                    closeIcon = closeIcon,
                    focusRequester = focusRequester,
                    textState = textState,
                    onTextChange = onTextChange
                )

                LaunchedEffect(focusRequester) {
                    withContext(Dispatchers.IO) {
                        delay(500)
                    }
                    focusRequester.requestFocus()
                }
            } else {
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    focusRequester: FocusRequester,
    textState: MutableState<String>,
    close: () -> Unit,
    clearIcon: ImageVector,
    closeIcon: ImageVector,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = textState.value,
        onValueChange = {
            textState.value = it
            onTextChange(textState.value)
        },
        modifier = Modifier.focusRequester(focusRequester),
        placeholder = {
            Text(
                text = "Search",
                style = MaterialTheme.typography.labelLarge
            )
        },
        singleLine = true,
        colors = searchTextFieldColors(),
        keyboardOptions = KeyboardOptions(autoCorrectEnabled = false, imeAction = ImeAction.Search),
        leadingIcon = {
            IconButton(
                onClick = close
            ) {
                Icon(
                    imageVector = closeIcon,
                    contentDescription = null
                )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    textState.value = ""
                    onTextChange(textState.value)
                }
            ) {
                Icon(
                    imageVector = clearIcon,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun searchTextFieldColors(
    contentColor: Color = LocalContentColor.current
): TextFieldColors {
    return TextFieldDefaults.colors(
        unfocusedTextColor = contentColor,
        focusedTextColor = contentColor,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        cursorColor = contentColor,
        selectionColors = TextSelectionColors(
            handleColor = contentColor,
            backgroundColor = contentColor.copy(alpha = 0.3f)
        ),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = contentColor,
        unfocusedLeadingIconColor = contentColor,
        focusedTrailingIconColor = contentColor,
        unfocusedTrailingIconColor = contentColor,
        unfocusedPlaceholderColor = contentColor.copy(alpha = 0.5F),
        focusedPlaceholderColor = contentColor.copy(alpha = 0.5F),
    )
}