package com.example.browser

import android.graphics.Bitmap
import com.example.privacy.BlockedTracker
import java.util.UUID

enum class SearchEngine(val displayName: String, val searchUrl: String, val homeUrl: String) {
    DUCKDUCKGO("DuckDuckGo (Privacy)", "https://duckduckgo.com/?q=", "https://duckduckgo.com"),
    BRAVE("Brave Search (Independent)", "https://search.brave.com/search?q=", "https://search.brave.com"),
    STARTPAGE("Startpage (Anonymous)", "https://www.startpage.com/sp/search?query=", "https://www.startpage.com"),
    SEARXNG("SearXNG (Decentralized)", "https://searx.be/search?q=", "https://searx.be"),
    GOOGLE("Google Search", "https://www.google.com/search?q=", "https://www.google.com")
}

enum class UserAgentOption(val label: String, val userAgentString: String) {
    STEALTH_MOBILE(
        "Stealth Mobile (Android)",
        "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36 StealthNet/1.0"
    ),
    DESKTOP_LINUX(
        "Desktop (Linux / Firefox Focus)",
        "Mozilla/5.0 (X11; Linux x86_64; rv:128.0) Gecko/20100101 Firefox/128.0"
    ),
    DESKTOP_MAC(
        "Desktop (Mac OS X Safari)",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Safari/605.1.15"
    ),
    ANDROID_TV(
        "Android TV / Leanback",
        "Mozilla/5.0 (Linux; Android 14; BRAVIA 4K Build/UR3_4K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 LargeScreen Safari/537.36"
    )
}

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val url: String = "about:home",
    val title: String = "New Tab",
    val favicon: Bitmap? = null,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val blockedCount: Int = 0,
    val blockedList: List<BlockedTracker> = emptyList(),
    val isSecureHttps: Boolean = true,
    val isDesktopMode: Boolean = false
)

data class QuickShortcut(
    val title: String,
    val url: String,
    val iconName: String,
    val category: String
)

val DEFAULT_SHORTCUTS = listOf(
    QuickShortcut("DuckDuckGo", "https://duckduckgo.com", "search", "Search"),
    QuickShortcut("Proton Privacy", "https://proton.me", "vpn", "Privacy"),
    QuickShortcut("Wikipedia", "https://www.wikipedia.org", "book", "Knowledge"),
    QuickShortcut("Brave Search", "https://search.brave.com", "shield", "Search"),
    QuickShortcut("Reddit", "https://old.reddit.com", "forum", "Community"),
    QuickShortcut("GitHub", "https://github.com", "code", "Development"),
    QuickShortcut("Archive.org", "https://archive.org", "library", "Archive"),
    QuickShortcut("Hacker News", "https://news.ycombinator.com", "article", "Tech")
)
