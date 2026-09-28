package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LosslessCardBorder
import com.example.ui.theme.MetallicCyanPrimary

@Composable
fun ServerSettingsDialog(
    currentUrl: String,
    onSave: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var textValue by remember(currentUrl) { mutableStateOf(currentUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MetallicCyanPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.server_settings_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Specify the website server address to connect to. By default, it connects to your free InfinityFree hosting instance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Server URL") },
                    placeholder = { Text(stringResource(id = R.string.server_settings_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSave(textValue) }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MetallicCyanPrimary,
                        unfocusedBorderColor = LosslessCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("server_url_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.testTag("reset_server_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MetallicCyanPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.btn_reset),
                        color = MetallicCyanPrimary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(textValue) },
                colors = ButtonDefaults.buttonColors(containerColor = MetallicCyanPrimary),
                modifier = Modifier.testTag("save_server_button")
            ) {
                Text(
                    text = stringResource(id = R.string.btn_save),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_server_button")
            ) {
                Text(stringResource(id = R.string.btn_close))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("server_settings_dialog")
    )
}

@Composable
fun AboutDialog(
    serverUrl: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.about_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MetallicCyanPrimary,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.about_dialog_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MetallicCyanPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Formats: FLAC, ALAC, WAV, Hi-Res Audio",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MetallicCyanPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = serverUrl,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MetallicCyanPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Contact: mimusicacompartida1@gmail.com",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MetallicCyanPrimary),
                modifier = Modifier.testTag("about_dialog_close_button")
            ) {
                Text(
                    text = stringResource(id = R.string.btn_close),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("about_dialog")
    )
}
