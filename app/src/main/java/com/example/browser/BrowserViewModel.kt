package com.example.browser

import android.content.Context
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Bookmark
import com.example.data.BookmarkRepository
import com.example.privacy.BlockedTracker
import com.example.privacy.DnsProvider
import com.example.privacy.ProxySettings
import com.example.privacy.StealthVpnService
import com.example.privacy.VpnStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowserViewModel(
    private val repository: BookmarkRepository
) : ViewModel() {

    private val initialTab = BrowserTab(
        id = "tab_initial",
        url = "about:home",
        title = "Stealth Home"
    )

    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(initialTab))
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow<String>(initialTab.id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    val activeTab: StateFlow<BrowserTab?> = combine(_tabs, _activeTabId) { tabs, id ->
        tabs.find { it.id == id } ?: tabs.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialTab)

    // Privacy & Shield States
    private val _shieldEnabled = MutableStateFlow(true)
    val shieldEnabled: StateFlow<Boolean> = _shieldEnabled.asStateFlow()

    private val _blockThirdPartyCookies = MutableStateFlow(true)
    val blockThirdPartyCookies: StateFlow<Boolean> = _blockThirdPartyCookies.asStateFlow()

    private val _webrtcLeakProtection = MutableStateFlow(true)
    val webrtcLeakProtection: StateFlow<Boolean> = _webrtcLeakProtection.asStateFlow()

    private val _blockImages = MutableStateFlow(false)
    val blockImages: StateFlow<Boolean> = _blockImages.asStateFlow()

    private val _javascriptEnabled = MutableStateFlow(true)
    val javascriptEnabled: StateFlow<Boolean> = _javascriptEnabled.asStateFlow()

    private val _searchEngine = MutableStateFlow(SearchEngine.DUCKDUCKGO)
    val searchEngine: StateFlow<SearchEngine> = _searchEngine.asStateFlow()

    private val _userAgentOption = MutableStateFlow(UserAgentOption.STEALTH_MOBILE)
    val userAgentOption: StateFlow<UserAgentOption> = _userAgentOption.asStateFlow()

    private val _dnsProvider = MutableStateFlow(DnsProvider.CLOUDFLARE)
    val dnsProvider: StateFlow<DnsProvider> = _dnsProvider.asStateFlow()

    private val _proxySettings = MutableStateFlow(ProxySettings())
    val proxySettings: StateFlow<ProxySettings> = _proxySettings.asStateFlow()

    private val _totalTrackersBlocked = MutableStateFlow(0)
    val totalTrackersBlocked: StateFlow<Int> = _totalTrackersBlocked.asStateFlow()

    // Flash Player & Media Engine
    private val _flashPlayerEnabled = MutableStateFlow(true)
    val flashPlayerEnabled: StateFlow<Boolean> = _flashPlayerEnabled.asStateFlow()

    // Desktop vs Mobile Responsive View
    private val _isDesktopMode = MutableStateFlow(false)
    val isDesktopMode: StateFlow<Boolean> = _isDesktopMode.asStateFlow()

    // TV & Display mode
    private val _isTvMode = MutableStateFlow(false)
    val isTvMode: StateFlow<Boolean> = _isTvMode.asStateFlow()

    private val _zoomPercent = MutableStateFlow(100)
    val zoomPercent: StateFlow<Int> = _zoomPercent.asStateFlow()

    // Bookmarks Flow
    val bookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Tab Navigation Event (triggered from UI)
    sealed class WebAction {
        data class LoadUrl(val url: String) : WebAction()
        object GoBack : WebAction()
        object GoForward : WebAction()
        object Reload : WebAction()
        object Stop : WebAction()
        data class SetZoom(val percent: Int) : WebAction()
        object ScrollUp : WebAction()
        object ScrollDown : WebAction()
    }

    private val _webAction = MutableStateFlow<WebAction?>(null)
    val webAction: StateFlow<WebAction?> = _webAction.asStateFlow()

    fun consumeWebAction() {
        _webAction.value = null
    }

    fun openNewTab(url: String = "about:home") {
        val newTab = BrowserTab(url = url, title = if (url == "about:home") "Stealth Home" else url)
        _tabs.value = _tabs.value + newTab
        _activeTabId.value = newTab.id
    }

    fun closeTab(tabId: String) {
        val currentTabs = _tabs.value
        if (currentTabs.size <= 1) {
            // If closing the last tab, reset to clean home tab
            val freshTab = BrowserTab(url = "about:home", title = "Stealth Home")
            _tabs.value = listOf(freshTab)
            _activeTabId.value = freshTab.id
            return
        }

        val updatedTabs = currentTabs.filter { it.id != tabId }
        _tabs.value = updatedTabs

        if (_activeTabId.value == tabId) {
            _activeTabId.value = updatedTabs.last().id
        }
    }

    fun closeAllTabs() {
        val freshTab = BrowserTab(url = "about:home", title = "Stealth Home")
        _tabs.value = listOf(freshTab)
        _activeTabId.value = freshTab.id
    }

    fun selectTab(tabId: String) {
        _activeTabId.value = tabId
    }

    fun navigateTo(input: String) {
        val trimmed = input.trim()
        val url = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            trimmed == "about:home" || trimmed == "about:blank" -> trimmed
            else -> _searchEngine.value.searchUrl + java.net.URLEncoder.encode(trimmed, "UTF-8")
        }

        val activeId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == activeId) {
                tab.copy(url = url, isLoading = url != "about:home")
            } else tab
        }

        if (url != "about:home") {
            _webAction.value = WebAction.LoadUrl(url)
        }
    }

    fun goBack() {
        _webAction.value = WebAction.GoBack
    }

    fun goForward() {
        _webAction.value = WebAction.GoForward
    }

    fun reload() {
        _webAction.value = WebAction.Reload
    }

    fun stop() {
        _webAction.value = WebAction.Stop
    }

    fun scrollUp() {
        _webAction.value = WebAction.ScrollUp
    }

    fun scrollDown() {
        _webAction.value = WebAction.ScrollDown
    }

    fun setZoom(percent: Int) {
        val clamped = percent.coerceIn(50, 300)
        _zoomPercent.value = clamped
        _webAction.value = WebAction.SetZoom(clamped)
    }

    fun toggleTvMode() {
        val newMode = !_isTvMode.value
        _isTvMode.value = newMode
        if (newMode) {
            _userAgentOption.value = UserAgentOption.ANDROID_TV
            setZoom(150) // Comfort zoom for 10-foot TV experience
        } else {
            _userAgentOption.value = UserAgentOption.STEALTH_MOBILE
            setZoom(100)
        }
    }

    fun toggleFlashPlayer() {
        _flashPlayerEnabled.value = !_flashPlayerEnabled.value
        reload()
    }

    fun toggleDesktopMode() {
        val next = !_isDesktopMode.value
        _isDesktopMode.value = next
        if (next) {
            _userAgentOption.value = UserAgentOption.DESKTOP_LINUX
        } else {
            _userAgentOption.value = if (_isTvMode.value) UserAgentOption.ANDROID_TV else UserAgentOption.STEALTH_MOBILE
        }
        reload()
    }

    fun updateActiveTabState(
        url: String? = null,
        title: String? = null,
        favicon: Bitmap? = null,
        isLoading: Boolean? = null,
        progress: Int? = null,
        canGoBack: Boolean? = null,
        canGoForward: Boolean? = null,
        isSecure: Boolean? = null
    ) {
        val currentId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == currentId) {
                tab.copy(
                    url = url ?: tab.url,
                    title = title ?: tab.title,
                    favicon = favicon ?: tab.favicon,
                    isLoading = isLoading ?: tab.isLoading,
                    progress = progress ?: tab.progress,
                    canGoBack = canGoBack ?: tab.canGoBack,
                    canGoForward = canGoForward ?: tab.canGoForward,
                    isSecureHttps = isSecure ?: tab.isSecureHttps
                )
            } else tab
        }
    }

    fun recordBlockedTracker(tracker: BlockedTracker) {
        val currentId = _activeTabId.value
        _totalTrackersBlocked.value += 1
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == currentId) {
                val updatedList = (tab.blockedList + tracker).takeLast(50)
                tab.copy(
                    blockedCount = tab.blockedCount + 1,
                    blockedList = updatedList
                )
            } else tab
        }
    }

    fun resetTabBlockedCount() {
        val currentId = _activeTabId.value
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == currentId) {
                tab.copy(blockedCount = 0, blockedList = emptyList())
            } else tab
        }
    }

    fun toggleBookmark(url: String, title: String) {
        viewModelScope.launch {
            val exists = bookmarks.value.any { it.url == url }
            if (exists) {
                repository.removeBookmarkByUrl(url)
            } else {
                repository.addBookmark(title, url)
            }
        }
    }

    fun removeBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.removeBookmark(bookmark)
        }
    }

    fun setDnsProvider(provider: DnsProvider) {
        _dnsProvider.value = provider
    }

    fun toggleShield() {
        _shieldEnabled.value = !_shieldEnabled.value
    }

    fun toggleThirdPartyCookies() {
        _blockThirdPartyCookies.value = !_blockThirdPartyCookies.value
    }

    fun toggleWebRtcProtection() {
        _webrtcLeakProtection.value = !_webrtcLeakProtection.value
    }

    fun toggleBlockImages() {
        _blockImages.value = !_blockImages.value
        reload()
    }

    fun toggleJavascript() {
        _javascriptEnabled.value = !_javascriptEnabled.value
        reload()
    }

    fun setSearchEngine(engine: SearchEngine) {
        _searchEngine.value = engine
    }

    fun setUserAgent(option: UserAgentOption) {
        _userAgentOption.value = option
        reload()
    }

    fun updateProxy(settings: ProxySettings) {
        _proxySettings.value = settings
    }

    /**
     * Erases all browsing data completely (stealth purge):
     * Wipes all open tabs, clears WebView cache, clear cookies, WebStorage, DOM storage.
     */
    fun eraseSession(context: Context) {
        viewModelScope.launch {
            try {
                // Clear CookieManager
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()

                // Clear WebStorage (local storage, IndexedDB, etc.)
                WebStorage.getInstance().deleteAllData()

                // Clear WebView default cache
                val dummyWebView = WebView(context)
                dummyWebView.clearCache(true)
                dummyWebView.clearFormData()
                dummyWebView.clearHistory()
                dummyWebView.destroy()
            } catch (_: Exception) {}

            closeAllTabs()
        }
    }
}
