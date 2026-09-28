package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MetallicCyanPrimary
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LosslessTopBar(
    title: String,
    currentUrl: String,
    isLoading: Boolean,
    progress: Int,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isOnline: Boolean,
    isDesktopMode: Boolean,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onStopClick: () -> Unit,
    onHomeClick: () -> Unit,
    onDesktopModeToggle: () -> Unit,
    onClearCacheClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            ),
            navigationIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }
                }
            },
            title = {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .testTag("app_title_container")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetallicCyanPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Online / Offline Indicator Pill
                        Surface(
                            shape = CircleShape,
                            color = (if (isOnline) StatusGreen else StatusRed).copy(alpha = 0.2f),
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) StatusGreen else StatusRed)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOnline) "ONLINE" else "OFFLINE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isOnline) StatusGreen else StatusRed
                                )
                            }
                        }
                    }

                    Text(
                        text = if (title.isNotBlank() && title != stringResource(id = R.string.app_name)) {
                            title
                        } else {
                            cleanDisplayUrl(currentUrl)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            actions = {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    enabled = canGoBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.action_back),
                        tint = if (canGoBack) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                // Forward Button
                IconButton(
                    onClick = onForwardClick,
                    enabled = canGoForward,
                    modifier = Modifier.testTag("nav_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(id = R.string.action_forward),
                        tint = if (canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                // Refresh / Stop Button
                IconButton(
                    onClick = { if (isLoading) onStopClick() else onRefreshClick() },
                    modifier = Modifier.testTag("nav_refresh_button")
                ) {
                    if (isLoading) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.action_stop),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.action_refresh),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Overflow Menu
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("overflow_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.testTag("overflow_dropdown_menu")
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.action_home)) },
                        leadingIcon = {
                            Icon(Icons.Default.Home, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            onHomeClick()
                        },
                        modifier = Modifier.testTag("menu_item_home")
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.action_share)) },
                        leadingIcon = {
                            Icon(Icons.Default.Share, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            shareCurrentPage(context, title, currentUrl)
                        },
                        modifier = Modifier.testTag("menu_item_share")
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.action_open_browser)) },
                        leadingIcon = {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            openInExternalBrowser(context, currentUrl)
                        },
                        modifier = Modifier.testTag("menu_item_browser")
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isDesktopMode) stringResource(id = R.string.action_mobile_mode)
                                else stringResource(id = R.string.action_desktop_mode)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                if (isDesktopMode) Icons.Default.Smartphone else Icons.Default.Computer,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onDesktopModeToggle()
                        },
                        modifier = Modifier.testTag("menu_item_desktop_mode")
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.action_clear_cache)) },
                        leadingIcon = {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            onClearCacheClick()
                        },
                        modifier = Modifier.testTag("menu_item_clear_cache")
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.action_server_settings)) },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            onSettingsClick()
                        },
                        modifier = Modifier.testTag("menu_item_settings")
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.action_about)) },
                        leadingIcon = {
                            Icon(Icons.Default.Info, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            onAboutClick()
                        },
                        modifier = Modifier.testTag("menu_item_about")
                    )
                }
            }
        )

        // Loading progress bar
        if (isLoading && progress < 100) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                color = MetallicCyanPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .testTag("loading_progress_bar")
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

private fun cleanDisplayUrl(url: String): String {
    return try {
        val uri = Uri.parse(url)
        val host = uri.host ?: url
        val path = uri.path ?: ""
        if (path.length > 1) "$host$path" else host
    } catch (_: Exception) {
        url
    }
}

private fun shareCurrentPage(context: Context, title: String, url: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "$title\n$url")
        }
        context.startActivity(Intent.createChooser(intent, "Share Lossless Release"))
    } catch (_: Exception) {
    }
}

private fun openInExternalBrowser(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
