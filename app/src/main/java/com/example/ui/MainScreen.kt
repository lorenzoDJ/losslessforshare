package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AboutDialog
import com.example.ui.components.ErrorView
import com.example.ui.components.LosslessTopBar
import com.example.ui.components.ServerSettingsDialog
import com.example.ui.components.WebViewContainer
import com.example.viewmodel.WebViewModel

@Composable
fun MainScreen(
    viewModel: WebViewModel,
    onFinishActivity: () -> Unit = {}
) {
    val serverUrl by viewModel.serverUrl.collectAsStateWithLifecycle()
    val currentUrl by viewModel.currentUrl.collectAsStateWithLifecycle()
    val pageTitle by viewModel.pageTitle.collectAsStateWithLifecycle()
    val loadingProgress by viewModel.loadingProgress.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val canGoBack by viewModel.canGoBack.collectAsStateWithLifecycle()
    val canGoForward by viewModel.canGoForward.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val isDesktopMode by viewModel.isDesktopMode.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showAboutDialog by viewModel.showAboutDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept hardware/system back button
    BackHandler(enabled = true) {
        if (canGoBack) {
            viewModel.goBack()
        } else if (errorMessage != null) {
            viewModel.reload()
        } else {
            onFinishActivity()
        }
    }

    // Listen for toast/snackbar messages
    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            LosslessTopBar(
                title = pageTitle,
                currentUrl = currentUrl,
                isLoading = isLoading,
                progress = loadingProgress,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                isOnline = isOnline,
                isDesktopMode = isDesktopMode,
                onBackClick = { viewModel.goBack() },
                onForwardClick = { viewModel.goForward() },
                onRefreshClick = { viewModel.reload() },
                onStopClick = { viewModel.stopLoading() },
                onHomeClick = { viewModel.goHome() },
                onDesktopModeToggle = { viewModel.toggleDesktopMode() },
                onClearCacheClick = { viewModel.clearCacheAndReload() },
                onSettingsClick = { viewModel.openSettingsDialog() },
                onAboutClick = { viewModel.openAboutDialog() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // WebView layer
            WebViewContainer(
                initialUrl = serverUrl,
                isDesktopMode = isDesktopMode,
                commands = viewModel.commands,
                onPageStarted = { url -> viewModel.onPageStarted(url) },
                onPageFinished = { url -> viewModel.onPageFinished(url) },
                onProgressChanged = { progress -> viewModel.onProgressChanged(progress) },
                onTitleReceived = { title -> viewModel.onTitleReceived(title) },
                onNavigationStateChanged = { canBack, canForward ->
                    viewModel.onNavigationStateChanged(canBack, canForward)
                },
                onErrorReceived = { description -> viewModel.onErrorReceived(description) },
                onDownloadTriggered = { fileName ->
                    viewModel.notifyUser("Download started: $fileName")
                }
            )

            // Show Error / Offline View overlay if the page failed to load
            if (errorMessage != null || (!isOnline && currentUrl.isBlank())) {
                ErrorView(
                    isOnline = isOnline,
                    errorMessage = errorMessage,
                    serverUrl = serverUrl,
                    onRetryClick = { viewModel.reload() },
                    onClearCacheClick = { viewModel.clearCacheAndReload() },
                    onSettingsClick = { viewModel.openSettingsDialog() }
                )
            }
        }
    }

    // Settings Dialog
    if (showSettingsDialog) {
        ServerSettingsDialog(
            currentUrl = serverUrl,
            onSave = { newUrl -> viewModel.saveServerUrl(newUrl) },
            onReset = { viewModel.resetServerUrl() },
            onDismiss = { viewModel.closeSettingsDialog() }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AboutDialog(
            serverUrl = serverUrl,
            onDismiss = { viewModel.closeAboutDialog() }
        )
    }
}
