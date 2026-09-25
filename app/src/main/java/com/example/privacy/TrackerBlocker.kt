package com.example.privacy

import android.net.Uri

enum class TrackerCategory(val displayName: String, val colorHex: Long) {
    ANALYTICS("Analytics & Telemetry", 0xFF6366F1),
    AD_NETWORK("Advertising Network", 0xFFF59E0B),
    SOCIAL_TRACKER("Social Widget Tracker", 0xFFA855F7),
    FINGERPRINTING("Device Fingerprinting", 0xFFEF4444),
    OTHER("Cross-Site Tracker", 0xFF3B82F6)
}

data class BlockedTracker(
    val url: String,
    val domain: String,
    val category: TrackerCategory,
    val timestamp: Long = System.currentTimeMillis()
)

object TrackerBlocker {

    private val AD_DOMAINS = hashSetOf(
        "doubleclick.net",
        "googleads.g.doubleclick.net",
        "pagead2.googlesyndication.com",
        "adservice.google.com",
        "criteo.com",
        "criteo.net",
        "outbrain.com",
        "taboola.com",
        "adroll.com",
        "adnxs.com",
        "rubiconproject.com",
        "pubmatic.com",
        "openx.net",
        "casalemedia.com",
        "popads.net",
        "propellerads.com",
        "mgid.com",
        "revcontent.com",
        "advertising.com",
        "media.net",
        "smartadserver.com",
        "moatads.com",
        "serving-sys.com",
        "yieldmo.com",
        "amazon-adsystem.com"
    )

    private val ANALYTICS_DOMAINS = hashSetOf(
        "google-analytics.com",
        "googletagmanager.com",
        "analytics.google.com",
        "hotjar.com",
        "clarity.ms",
        "segment.io",
        "segment.com",
        "mixpanel.com",
        "scorecardresearch.com",
        "quantserve.com",
        "chartbeat.com",
        "newrelic.com",
        "nr-data.net",
        "statcounter.com",
        "kissmetrics.com",
        "crazyegg.com",
        "amplitude.com",
        "fullstory.com",
        "mouseflow.com",
        "heapanalytics.com",
        "optimizely.com",
        "branch.io",
        "appsflyer.com",
        "flurry.com",
        "yandex.ru/metrika",
        "mc.yandex.ru"
    )

    private val SOCIAL_TRACKERS = hashSetOf(
        "connect.facebook.net",
        "facebook.com/tr",
        "graph.facebook.com",
        "pixel.facebook.com",
        "analytics.twitter.com",
        "static.ads-twitter.com",
        "platform.twitter.com/widgets",
        "analytics.tiktok.com",
        "business-api.tiktok.com",
        "tr.snapchat.com",
        "sc-static.net",
        "linkedin.com/px",
        "licdn.com"
    )

    private val FINGERPRINTING_DOMAINS = hashSetOf(
        "fpjs.io",
        "fingerprintjs.com",
        "api.fpjs.io",
        "cdn.fpjs.io",
        "threatmetrix.com",
        "iovation.com",
        "perimeterx.net",
        "kasada.io",
        "siftscience.com"
    )

    fun checkUrl(rawUrl: String): BlockedTracker? {
        val uri = try {
            Uri.parse(rawUrl)
        } catch (_: Exception) {
            return null
        }
        val host = uri.host?.lowercase() ?: return null

        // Check exact match or suffix domain match
        fun matches(set: Set<String>): Boolean {
            return set.any { host == it || host.endsWith(".$it") }
        }

        val category = when {
            matches(AD_DOMAINS) -> TrackerCategory.AD_NETWORK
            matches(ANALYTICS_DOMAINS) -> TrackerCategory.ANALYTICS
            matches(SOCIAL_TRACKERS) -> TrackerCategory.SOCIAL_TRACKER
            matches(FINGERPRINTING_DOMAINS) -> TrackerCategory.FINGERPRINTING
            uri.path?.contains("pixel") == true || uri.path?.contains("/telemetry") == true || uri.path?.contains("/beacon") == true -> TrackerCategory.OTHER
            else -> null
        }

        return category?.let {
            BlockedTracker(
                url = rawUrl,
                domain = host,
                category = it
            )
        }
    }
}
