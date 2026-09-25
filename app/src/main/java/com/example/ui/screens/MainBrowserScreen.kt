package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.browser.BrowserViewModel
import com.example.privacy.StealthVpnService
import com.example.ui.components.BrowserBottomBar
import com.example.ui.components.BrowserTopBar
import com.example.ui.components.BrowserWebView
import com.example.ui.components.TvRemoteOverlay
import com.example.ui.sheets.BookmarksSheet
import com.example.ui.sheets.SettingsSheet
import com.example.ui.sheets.ShieldSheet
import com.example.ui.sheets.TabManagerSheet
import com.example.ui.sheets.VpnTunnelSheet
import com.example.ui.theme.ElectricCyan

@Composable
fun MainBrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()

    val shieldEnabled by viewModel.shieldEnabled.collectAsState()
    val blockThirdPartyCookies by viewModel.blockThirdPartyCookies.collectAsState()
    val webrtcLeakProtection by viewModel.webrtcLeakProtection.collectAsState()
    val blockImages by viewModel.blockImages.collectAsState()
    val javascriptEnabled by viewModel.javascriptEnabled.collectAsState()
    val searchEngine by viewModel.searchEngine.collectAsState()
    val userAgent by viewModel.userAgentOption.collectAsState()
    val dnsProvider by viewModel.dnsProvider.collectAsState()
    val proxySettings by viewModel.proxySettings.collectAsState()
    val lifetimeBlocked by viewModel.totalTrackersBlocked.collectAsState()
    val isTvMode by viewModel.isTvMode.collectAsState()
    val zoomPercent by viewModel.zoomPercent.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val webAction by viewModel.webAction.collectAsState()
    val flashPlayerEnabled by viewModel.flashPlayerEnabled.collectAsState()
    val isDesktopMode by viewModel.isDesktopMode.collectAsState()

    // Observe global VPN tunnel status
    val vpnStatus by StealthVpnService.vpnStatus.collectAsState()

    // Sheet states
    var showShieldSheet by remember { mutableStateOf(false) }
    var showVpnSheet by remember { mutableStateOf(false) }
    var showTabsSheet by remember { mutableStateOf(false) }
    var showBookmarksSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showEraseDialog by remember { mutableStateOf(false) }

    val isCurrentBookmarked = bookmarks.any { it.url == activeTab?.url }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            BrowserTopBar(
                tab = activeTab,
                tabCount = tabs.size,
                shieldEnabled = shieldEnabled,
                isBookmarked = isCurrentBookmarked,
                onNavigate = { viewModel.navigateTo(it) },
                onReload = { viewModel.reload() },
                onStop = { viewModel.stop() },
                onOpenShield = { showShieldSheet = true },
                onOpenTabs = { showTabsSheet = true },
                onEraseSession = { showEraseDialog = true },
                onToggleBookmark = {
                    activeTab?.let { tab ->
                        viewModel.toggleBookmark(tab.url, tab.title)
                        Toast.makeText(
                            context,
                            if (isCurrentBookmarked) "Bookmark removed" else "Page bookmarked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onOpenBookmarks = { showBookmarksSheet = true },
                onOpenVpnHub = { showVpnSheet = true },
                onOpenSettings = { showSettingsSheet = true },
                onToggleTvMode = { viewModel.toggleTvMode() },
                isDesktopMode = isDesktopMode,
                onToggleDesktopMode = { viewModel.toggleDesktopMode() }
            )
        },
        bottomBar = {
            if (!isTvMode) {
                BrowserBottomBar(
                    canGoBack = activeTab?.canGoBack == true,
                    canGoForward = activeTab?.canGoForward == true,
                    isBookmarked = isCurrentBookmarked,
                    vpnStatus = vpnStatus,
                    onBack = { viewModel.goBack() },
                    onForward = { viewModel.goForward() },
                    onHome = { viewModel.navigateTo("about:home") },
                    onOpenVpnHub = { showVpnSheet = true },
                    onToggleBookmark = {
                        activeTab?.let { tab ->
                            viewModel.toggleBookmark(tab.url, tab.title)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (activeTab?.url == "about:home") {
                StealthHomeScreen(
                    searchEngine = searchEngine,
                    dnsProvider = dnsProvider,
                    vpnStatus = vpnStatus,
                    lifetimeBlocked = lifetimeBlocked,
                    bookmarks = bookmarks,
                    onSearch = { viewModel.navigateTo(it) },
                    onOpenUrl = { viewModel.navigateTo(it) },
                    onOpenVpnHub = { showVpnSheet = true },
                    onOpenBookmarks = { showBookmarksSheet = true }
                )
            } else {
                activeTab?.let { currentTab ->
                    BrowserWebView(
                        tab = currentTab,
                        shieldEnabled = shieldEnabled,
                        blockThirdPartyCookies = blockThirdPartyCookies,
                        webrtcLeakProtection = webrtcLeakProtection,
                        flashPlayerEnabled = flashPlayerEnabled,
                        blockImages = blockImages,
                        javascriptEnabled = javascriptEnabled,
                        userAgentOption = userAgent,
                        zoomPercent = zoomPercent,
                        webAction = webAction,
                        onActionConsumed = { viewModel.consumeWebAction() },
                        onTrackerBlocked = { tracker ->
                            viewModel.recordBlockedTracker(tracker)
                        },
                        onTabStateUpdate = { url, title, favicon, isLoading, progress, canGoBack, canGoForward, isSecure ->
                            viewModel.updateActiveTabState(
                                url = url,
                                title = title,
                                favicon = favicon,
                                isLoading = isLoading,
                                progress = progress,
                                canGoBack = canGoBack,
                                canGoForward = canGoForward,
                                isSecure = isSecure
                            )
                        }
                    )
                }
            }

            // TV Remote Overlay if TV Mode is active
            if (isTvMode) {
                TvRemoteOverlay(
                    zoomPercent = zoomPercent,
                    onScrollUp = { viewModel.scrollUp() },
                    onScrollDown = { viewModel.scrollDown() },
                    onZoomIn = { viewModel.setZoom(zoomPercent + 25) },
                    onZoomOut = { viewModel.setZoom(zoomPercent - 25) },
                    onBack = { viewModel.goBack() },
                    onForward = { viewModel.goForward() },
                    onHome = { viewModel.navigateTo("about:home") },
                    onReload = { viewModel.reload() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Erase Session Confirmation Dialog
    if (showEraseDialog) {
        AlertDialog(
            onDismissRequest = { showEraseDialog = false },
            title = {
                Text(
                    text = "Erase Browsing Session?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will immediately close all tabs, erase web cache, delete all cookies, purge DOM storage, and reset to a clean slate. No history will remain."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEraseDialog = false
                        viewModel.eraseSession(context)
                        Toast.makeText(context, "Session wiped cleanly", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                    modifier = Modifier.testTag("confirm_erase_btn")
                ) {
                    Text("Erase Everything", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEraseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal Sheets
    if (showShieldSheet) {
        ShieldSheet(
            tab = activeTab,
            shieldEnabled = shieldEnabled,
            blockThirdPartyCookies = blockThirdPartyCookies,
            webrtcLeakProtection = webrtcLeakProtection,
            flashPlayerEnabled = flashPlayerEnabled,
            blockImages = blockImages,
            javascriptEnabled = javascriptEnabled,
            onToggleShield = { viewModel.toggleShield() },
            onToggleCookies = { viewModel.toggleThirdPartyCookies() },
            onToggleWebRtc = { viewModel.toggleWebRtcProtection() },
            onToggleFlash = { viewModel.toggleFlashPlayer() },
            onToggleImages = { viewModel.toggleBlockImages() },
            onToggleJavascript = { viewModel.toggleJavascript() },
            onDismiss = { showShieldSheet = false }
        )
    }

    if (showVpnSheet) {
        VpnTunnelSheet(
            activeProvider = dnsProvider,
            proxySettings = proxySettings,
            vpnStatus = vpnStatus,
            onSelectProvider = { viewModel.setDnsProvider(it) },
            onUpdateProxy = { viewModel.updateProxy(it) },
            onDismiss = { showVpnSheet = false }
        )
    }

    if (showTabsSheet) {
        TabManagerSheet(
            tabs = tabs,
            activeTabId = activeTabId,
            onSelectTab = { viewModel.selectTab(it) },
            onCloseTab = { viewModel.closeTab(it) },
            onNewTab = { viewModel.openNewTab() },
            onCloseAllAndErase = {
                showTabsSheet = false
                viewModel.eraseSession(context)
                Toast.makeText(context, "All tabs closed & session erased", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showTabsSheet = false }
        )
    }

    if (showBookmarksSheet) {
        BookmarksSheet(
            bookmarks = bookmarks,
            onSelectBookmark = { viewModel.navigateTo(it) },
            onDeleteBookmark = { viewModel.removeBookmark(it) },
            onDismiss = { showBookmarksSheet = false }
        )
    }

    if (showSettingsSheet) {
        SettingsSheet(
            searchEngine = searchEngine,
            userAgent = userAgent,
            zoomPercent = zoomPercent,
            isTvMode = isTvMode,
            flashPlayerEnabled = flashPlayerEnabled,
            isDesktopMode = isDesktopMode,
            onSelectSearchEngine = { viewModel.setSearchEngine(it) },
            onSelectUserAgent = { viewModel.setUserAgent(it) },
            onSetZoom = { viewModel.setZoom(it) },
            onToggleTvMode = { viewModel.toggleTvMode() },
            onToggleFlash = { viewModel.toggleFlashPlayer() },
            onToggleDesktopMode = { viewModel.toggleDesktopMode() },
            onDismiss = { showSettingsSheet = false }
        )
    }
}
