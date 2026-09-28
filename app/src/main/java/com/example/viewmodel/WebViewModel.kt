package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.NetworkObserver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WebCommand {
    data object Reload : WebCommand
    data object StopLoading : WebCommand
    data object GoBack : WebCommand
    data object GoForward : WebCommand
    data object ClearCache : WebCommand
    data class LoadUrl(val url: String) : WebCommand
}

class WebViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val DEFAULT_SERVER_URL = "https://losslessforshare.great-site.net"
        private const val PREFS_NAME = "lossless_settings"
        private const val KEY_SERVER_URL = "server_url"
    }

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val networkObserver = NetworkObserver(application)

    private val _serverUrl = MutableStateFlow(
        prefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    )
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _currentUrl = MutableStateFlow(_serverUrl.value)
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    private val _pageTitle = MutableStateFlow("Lossless for Share")
    val pageTitle: StateFlow<String> = _pageTitle.asStateFlow()

    private val _loadingProgress = MutableStateFlow(0)
    val loadingProgress: StateFlow<Int> = _loadingProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isOnline = MutableStateFlow(networkObserver.isCurrentlyConnected())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isDesktopMode = MutableStateFlow(false)
    val isDesktopMode: StateFlow<Boolean> = _isDesktopMode.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showAboutDialog = MutableStateFlow(false)
    val showAboutDialog: StateFlow<Boolean> = _showAboutDialog.asStateFlow()

    private val _commands = MutableSharedFlow<WebCommand>()
    val commands: SharedFlow<WebCommand> = _commands.asSharedFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            networkObserver.isConnectedFlow.collect { connected ->
                _isOnline.value = connected
                if (connected && _errorMessage.value != null) {
                    // Automatically retry when connection restores
                    _errorMessage.value = null
                    _commands.emit(WebCommand.Reload)
                }
            }
        }
    }

    fun onPageStarted(url: String) {
        _currentUrl.value = url
        _isLoading.value = true
        _errorMessage.value = null
    }

    fun onPageFinished(url: String) {
        _currentUrl.value = url
        _isLoading.value = false
        _loadingProgress.value = 100
    }

    fun onProgressChanged(progress: Int) {
        _loadingProgress.value = progress
        _isLoading.value = progress < 100
    }

    fun onTitleReceived(title: String?) {
        if (!title.isNullOrBlank() && !title.startsWith("http")) {
            _pageTitle.value = title
        }
    }

    fun onNavigationStateChanged(canBack: Boolean, canForward: Boolean) {
        _canGoBack.value = canBack
        _canGoForward.value = canForward
    }

    fun onErrorReceived(description: String) {
        _isLoading.value = false
        _errorMessage.value = description
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun reload() {
        viewModelScope.launch {
            _errorMessage.value = null
            _commands.emit(WebCommand.Reload)
        }
    }

    fun stopLoading() {
        viewModelScope.launch {
            _commands.emit(WebCommand.StopLoading)
            _isLoading.value = false
        }
    }

    fun goHome() {
        viewModelScope.launch {
            _errorMessage.value = null
            _commands.emit(WebCommand.LoadUrl(_serverUrl.value))
        }
    }

    fun goBack() {
        viewModelScope.launch {
            _commands.emit(WebCommand.GoBack)
        }
    }

    fun goForward() {
        viewModelScope.launch {
            _commands.emit(WebCommand.GoForward)
        }
    }

    fun loadCustomUrl(url: String) {
        val formatted = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }
        viewModelScope.launch {
            _errorMessage.value = null
            _commands.emit(WebCommand.LoadUrl(formatted))
        }
    }

    fun toggleDesktopMode() {
        _isDesktopMode.value = !_isDesktopMode.value
        reload()
    }

    fun clearCacheAndReload() {
        viewModelScope.launch {
            _commands.emit(WebCommand.ClearCache)
            _errorMessage.value = null
            _commands.emit(WebCommand.Reload)
            _toastMessage.emit("Cache and cookies cleared")
        }
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun openAboutDialog() {
        _showAboutDialog.value = true
    }

    fun closeAboutDialog() {
        _showAboutDialog.value = false
    }

    fun saveServerUrl(newUrl: String) {
        var cleanUrl = newUrl.trim()
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            cleanUrl = "https://$cleanUrl"
        }
        _serverUrl.value = cleanUrl
        prefs.edit().putString(KEY_SERVER_URL, cleanUrl).apply()
        _showSettingsDialog.value = false
        loadCustomUrl(cleanUrl)
    }

    fun resetServerUrl() {
        _serverUrl.value = DEFAULT_SERVER_URL
        prefs.edit().putString(KEY_SERVER_URL, DEFAULT_SERVER_URL).apply()
        _showSettingsDialog.value = false
        loadCustomUrl(DEFAULT_SERVER_URL)
    }

    fun notifyUser(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}
