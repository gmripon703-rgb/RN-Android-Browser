package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.browser.BrowserTab
import com.example.browser.BrowserViewModel
import com.example.browser.UserAgentOption
import com.example.privacy.BlockedTracker
import com.example.privacy.StealthScripts
import com.example.privacy.TrackerBlocker
import java.io.ByteArrayInputStream

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    tab: BrowserTab,
    shieldEnabled: Boolean,
    blockThirdPartyCookies: Boolean,
    webrtcLeakProtection: Boolean,
    flashPlayerEnabled: Boolean,
    blockImages: Boolean,
    javascriptEnabled: Boolean,
    userAgentOption: UserAgentOption,
    zoomPercent: Int,
    webAction: BrowserViewModel.WebAction?,
    onActionConsumed: () -> Unit,
    onTrackerBlocked: (BlockedTracker) -> Unit,
    onTabStateUpdate: (
        url: String?,
        title: String?,
        favicon: Bitmap?,
        isLoading: Boolean?,
        progress: Int?,
        canGoBack: Boolean?,
        canGoForward: Boolean?,
        isSecure: Boolean?
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var customVideoView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }

    BackHandler(enabled = customVideoView != null || (webViewRef?.canGoBack() == true)) {
        if (customVideoView != null) {
            customViewCallback?.onCustomViewHidden()
            customVideoView = null
        } else if (webViewRef?.canGoBack() == true) {
            webViewRef?.goBack()
        }
    }

    LaunchedEffect(webAction) {
        val webView = webViewRef ?: return@LaunchedEffect
        when (webAction) {
            is BrowserViewModel.WebAction.LoadUrl -> {
                webView.loadUrl(webAction.url)
                onActionConsumed()
            }
            BrowserViewModel.WebAction.GoBack -> {
                if (webView.canGoBack()) webView.goBack()
                onActionConsumed()
            }
            BrowserViewModel.WebAction.GoForward -> {
                if (webView.canGoForward()) webView.goForward()
                onActionConsumed()
            }
            BrowserViewModel.WebAction.Reload -> {
                webView.reload()
                onActionConsumed()
            }
            BrowserViewModel.WebAction.Stop -> {
                webView.stopLoading()
                onActionConsumed()
            }
            is BrowserViewModel.WebAction.SetZoom -> {
                webView.settings.textZoom = webAction.percent
                onActionConsumed()
            }
            BrowserViewModel.WebAction.ScrollUp -> {
                webView.scrollBy(0, -300)
                onActionConsumed()
            }
            BrowserViewModel.WebAction.ScrollDown -> {
                webView.scrollBy(0, 300)
                onActionConsumed()
            }
            null -> {}
        }
    }

    LaunchedEffect(zoomPercent) {
        webViewRef?.settings?.textZoom = zoomPercent
    }

    LaunchedEffect(userAgentOption) {
        webViewRef?.settings?.userAgentString = userAgentOption.userAgentString
    }

    LaunchedEffect(blockImages) {
        webViewRef?.settings?.loadsImagesAutomatically = !blockImages
    }

    LaunchedEffect(javascriptEnabled) {
        webViewRef?.settings?.javaScriptEnabled = javascriptEnabled
    }

    DisposableEffect(tab.id) {
        onDispose {
            webViewRef?.stopLoading()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Enable D-Pad / Remote Focus for Android TV
                    isFocusable = true
                    isFocusableInTouchMode = true

                    settings.apply {
                        javaScriptEnabled = javascriptEnabled
                        domStorageEnabled = true
                        databaseEnabled = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        loadsImagesAutomatically = !blockImages
                        userAgentString = userAgentOption.userAgentString
                        mediaPlaybackRequiresUserGesture = false
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        textZoom = zoomPercent
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        allowFileAccess = true
                        allowContentAccess = true
                    }

                    CookieManager.getInstance().run {
                        setAcceptCookie(true)
                        setAcceptThirdPartyCookies(this@apply, !blockThirdPartyCookies)
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val requestUrl = request?.url?.toString() ?: return null

                            // Image blocking filter
                            if (blockImages) {
                                val path = request.url.path?.lowercase() ?: ""
                                if (path.endsWith(".png") || path.endsWith(".jpg") ||
                                    path.endsWith(".jpeg") || path.endsWith(".webp") ||
                                    path.endsWith(".gif") || path.endsWith(".svg")
                                ) {
                                    return WebResourceResponse(
                                        "image/png",
                                        "UTF-8",
                                        ByteArrayInputStream(ByteArray(0))
                                    )
                                }
                            }

                            // Privacy & Anti-Tracker Shield Filter
                            if (shieldEnabled) {
                                val blocked = TrackerBlocker.checkUrl(requestUrl)
                                if (blocked != null) {
                                    view?.post { onTrackerBlocked(blocked) }
                                    return WebResourceResponse(
                                        "text/plain",
                                        "UTF-8",
                                        ByteArrayInputStream(ByteArray(0))
                                    )
                                }
                            }

                            return super.shouldInterceptRequest(view, request)
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            val isSecure = url?.startsWith("https://") == true
                            onTabStateUpdate(
                                url,
                                null,
                                favicon,
                                true,
                                15,
                                view?.canGoBack(),
                                view?.canGoForward(),
                                isSecure
                            )

                            // Inject anti-fingerprinting and leak protections
                            if (webrtcLeakProtection) {
                                view?.evaluateJavascript(StealthScripts.PRIVACY_INJECTION_JS, null)
                            }
                            if (flashPlayerEnabled) {
                                view?.evaluateJavascript(StealthScripts.FLASH_EMULATOR_AND_RESPONSIVE_JS, null)
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            val isSecure = url?.startsWith("https://") == true
                            onTabStateUpdate(
                                url,
                                view?.title,
                                null,
                                false,
                                100,
                                view?.canGoBack(),
                                view?.canGoForward(),
                                isSecure
                            )

                            if (webrtcLeakProtection) {
                                view?.evaluateJavascript(StealthScripts.PRIVACY_INJECTION_JS, null)
                            }
                            if (flashPlayerEnabled) {
                                view?.evaluateJavascript(StealthScripts.FLASH_EMULATOR_AND_RESPONSIVE_JS, null)
                            }
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            onTabStateUpdate(
                                null, null, null,
                                newProgress < 100,
                                newProgress,
                                view?.canGoBack(),
                                view?.canGoForward(),
                                null
                            )
                        }

                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            onTabStateUpdate(null, title, null, null, null, null, null, null)
                        }

                        override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                            onTabStateUpdate(null, null, icon, null, null, null, null, null)
                        }

                        // Fullscreen video support for Mobile & TV (e.g. YouTube, Vimeo)
                        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                            customVideoView = view
                            customViewCallback = callback
                        }

                        override fun onHideCustomView() {
                            customViewCallback?.onCustomViewHidden()
                            customVideoView = null
                            customViewCallback = null
                        }
                    }

                    if (tab.url != "about:home") {
                        loadUrl(tab.url)
                    }

                    webViewRef = this
                }
            },
            update = { wv ->
                webViewRef = wv
            }
        )

        // Overlay for Fullscreen video if active
        if (customVideoView != null) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                factory = {
                    FrameLayout(it).apply {
                        addView(
                            customVideoView,
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                }
            )
        }
    }
}
