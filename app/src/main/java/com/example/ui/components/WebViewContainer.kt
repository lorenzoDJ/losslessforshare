package com.example.ui.components

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.viewmodel.WebCommand
import kotlinx.coroutines.flow.SharedFlow

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    initialUrl: String,
    isDesktopMode: Boolean,
    commands: SharedFlow<WebCommand>,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onTitleReceived: (String?) -> Unit,
    onNavigationStateChanged: (Boolean, Boolean) -> Unit,
    onErrorReceived: (String) -> Unit,
    onDownloadTriggered: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Essential settings for InfinityFree anti-bot JS and modern media sites
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = false
                mediaPlaybackRequiresUserGesture = false
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                cacheMode = WebSettings.LOAD_DEFAULT

                if (isDesktopMode) {
                    userAgentString = DESKTOP_USER_AGENT
                }
            }

            // Cookie configuration
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, true)

            // Setup download listener for music and archive files
            setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                handleDownload(context, url, userAgent, contentDisposition, mimetype, onDownloadTriggered)
            }
        }
    }

    // Update User Agent when desktop mode changes
    LaunchedEffect(isDesktopMode) {
        val originalUA = WebSettings.getDefaultUserAgent(context)
        webView.settings.userAgentString = if (isDesktopMode) DESKTOP_USER_AGENT else originalUA
    }

    // Connect WebViewClient and WebChromeClient
    DisposableEffect(webView) {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                url?.let {
                    onPageStarted(it)
                    onNavigationStateChanged(view?.canGoBack() == true, view?.canGoForward() == true)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                url?.let {
                    onPageFinished(it)
                    onNavigationStateChanged(view?.canGoBack() == true, view?.canGoForward() == true)
                    CookieManager.getInstance().flush()
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val uri = request?.url ?: return false
                val scheme = uri.scheme?.lowercase() ?: ""
                val urlString = uri.toString()

                // Intercept custom app schemes (tel, mailto, whatsapp, telegram, etc.)
                if (scheme !in listOf("http", "https")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        return true
                    } catch (_: Exception) {
                        return true
                    }
                }

                // Check if the URL points directly to an audio or archive file download
                val lowerUrl = urlString.lowercase()
                val isDirectMediaDownload = listOf(
                    ".flac", ".alac", ".wav", ".zip", ".rar", ".7z", ".mp3", ".m4a", ".aac", ".ogg"
                ).any { lowerUrl.contains(it) }

                if (isDirectMediaDownload) {
                    val userAgent = view?.settings?.userAgentString ?: ""
                    handleDownload(context, urlString, userAgent, null, null, onDownloadTriggered)
                    return true
                }

                return false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                // Only handle main frame failures
                if (request?.isForMainFrame == true) {
                    val description = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        error?.description?.toString() ?: "Connection error"
                    } else {
                        "Connection error"
                    }
                    onErrorReceived(description)
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                onProgressChanged(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                onTitleReceived(title)
            }
        }

        onDispose {
            webView.stopLoading()
        }
    }

    // Handle commands from ViewModel
    LaunchedEffect(commands) {
        commands.collect { command ->
            when (command) {
                is WebCommand.Reload -> webView.reload()
                is WebCommand.StopLoading -> webView.stopLoading()
                is WebCommand.GoBack -> if (webView.canGoBack()) webView.goBack()
                is WebCommand.GoForward -> if (webView.canGoForward()) webView.goForward()
                is WebCommand.ClearCache -> {
                    webView.clearCache(true)
                    webView.clearFormData()
                    webView.clearHistory()
                    CookieManager.getInstance().removeAllCookies(null)
                    CookieManager.getInstance().flush()
                }
                is WebCommand.LoadUrl -> webView.loadUrl(command.url)
            }
        }
    }

    // Initial load
    LaunchedEffect(initialUrl) {
        if (webView.url == null) {
            webView.loadUrl(initialUrl)
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
            .fillMaxSize()
            .testTag("main_webview")
    )
}

private fun handleDownload(
    context: Context,
    url: String,
    userAgent: String,
    contentDisposition: String?,
    mimetype: String?,
    onDownloadTriggered: (String) -> Unit
) {
    try {
        val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setMimeType(mimetype)
            val cookies = CookieManager.getInstance().getCookie(url)
            if (cookies != null) {
                addRequestHeader("cookie", cookies)
            }
            if (userAgent.isNotBlank()) {
                addRequestHeader("User-Agent", userAgent)
            }
            setDescription("Downloading from Lossless for Share")
            setTitle(fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        downloadManager?.enqueue(request)

        onDownloadTriggered(fileName)
        Toast.makeText(context, "Download started: $fileName", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        // Fallback: Open in external browser/downloader
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Unable to initiate download", Toast.LENGTH_SHORT).show()
        }
    }
}
