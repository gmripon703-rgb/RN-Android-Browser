package com.example.privacy

enum class DnsProvider(
    val title: String,
    val description: String,
    val primaryIp: String,
    val secondaryIp: String,
    val dohUrl: String,
    val latencyMs: Int
) {
    CLOUDFLARE(
        title = "Cloudflare 1.1.1.1",
        description = "World's fastest DNS. Never logs IP addresses or sells your data to ISPs.",
        primaryIp = "1.1.1.1",
        secondaryIp = "1.0.0.1",
        dohUrl = "https://cloudflare-dns.com/dns-query",
        latencyMs = 12
    ),
    CLOUDFLARE_FAMILY(
        title = "Cloudflare Security",
        description = "Blocks malware, phishing domains, and malicious cryptominers automatically.",
        primaryIp = "1.1.1.2",
        secondaryIp = "1.0.0.2",
        dohUrl = "https://security.cloudflare-dns.com/dns-query",
        latencyMs = 14
    ),
    QUAD9(
        title = "Quad9 Swiss Privacy",
        description = "Strict Swiss privacy laws, non-profit, zero telemetry & ISP protection.",
        primaryIp = "9.9.9.9",
        secondaryIp = "149.112.112.112",
        dohUrl = "https://dns.quad9.net/dns-query",
        latencyMs = 18
    ),
    ADGUARD_DNS(
        title = "AdGuard DNS Shield",
        description = "Blocks network-level ads, trackers, and malicious telemetries before loading.",
        primaryIp = "94.140.14.14",
        secondaryIp = "94.140.15.15",
        dohUrl = "https://dns.adguard.com/dns-query",
        latencyMs = 21
    ),
    PROTON_TOR(
        title = "Tor / Onion SOCKS Route",
        description = "Route DNS queries via local SOCKS5 proxy (127.0.0.1:9050) or Proton tunnel.",
        primaryIp = "127.0.0.1",
        secondaryIp = "127.0.0.1",
        dohUrl = "https://dns.adguard.com/dns-query",
        latencyMs = 45
    )
}

enum class ProxyType {
    DIRECT,
    SOCKS5,
    HTTP
}

data class ProxySettings(
    val enabled: Boolean = false,
    val type: ProxyType = ProxyType.SOCKS5,
    val host: String = "127.0.0.1",
    val port: Int = 9050,
    val username: String = "",
    val password: String = ""
)

object StealthScripts {
    val PRIVACY_INJECTION_JS = """
        (function() {
            try {
                // Global Privacy Control (GPC) & Do Not Track
                if (navigator) {
                    Object.defineProperty(navigator, 'globalPrivacyControl', { value: true, configurable: false });
                    Object.defineProperty(navigator, 'doNotTrack', { value: "1", configurable: false });
                }

                // Prevent Battery API Fingerprinting
                if (navigator.getBattery) {
                    navigator.getBattery = function() {
                        return Promise.reject(new Error("Battery API disabled for privacy"));
                    };
                }

                // Prevent WebRTC Local IP Leakage
                if (window.RTCPeerConnection) {
                    const OrigRTC = window.RTCPeerConnection;
                    window.RTCPeerConnection = function(config, constraints) {
                        if (config && config.iceServers) {
                            config.iceServers = [];
                        }
                        return new OrigRTC(config, constraints);
                    };
                    window.RTCPeerConnection.prototype = OrigRTC.prototype;
                }

                // Suppress automated browser detector flags
                Object.defineProperty(navigator, 'webdriver', { get: () => undefined });

                // Suppress invasive beacon tracking
                if (navigator.sendBeacon) {
                    const origBeacon = navigator.sendBeacon;
                    navigator.sendBeacon = function(url, data) {
                        console.log("[Browser RN] Blocked beacon to " + url);
                        return true;
                    };
                }
            } catch(e) {}
        })();
    """.trimIndent()

    val FLASH_EMULATOR_AND_RESPONSIVE_JS = """
        (function() {
            try {
                // 1. Mobile Responsive Viewport Enforcer
                if (!document.querySelector('meta[name="viewport"]')) {
                    const meta = document.createElement('meta');
                    meta.name = 'viewport';
                    meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes';
                    document.head ? document.head.appendChild(meta) : document.documentElement.appendChild(meta);
                }

                // 2. Open-source Flash Player (Ruffle) Integration
                // Check if page contains Flash content (.swf, shockwave-flash embed/object)
                const hasFlashElements = document.querySelector('embed[type*="flash"], object[type*="flash"], object[classid*="D27CDB6E"], embed[src*=".swf"], object[data*=".swf"], a[href*=".swf"]');
                const isSwfDirect = window.location.pathname.toLowerCase().endsWith('.swf');

                if (hasFlashElements || isSwfDirect) {
                    if (!window.RufflePlayer) {
                        window.RufflePlayer = window.RufflePlayer || {};
                        window.RufflePlayer.config = {
                            "autoplay": "on",
                            "unmuteOverlay": "visible",
                            "letterbox": "on",
                            "warnOnUnsupportedContent": false
                        };
                        const script = document.createElement('script');
                        script.src = 'https://unpkg.com/@ruffle-rs/ruffle';
                        script.async = true;
                        document.head ? document.head.appendChild(script) : document.documentElement.appendChild(script);
                        console.log("[Browser RN] Ruffle Flash Player Engine activated for legacy media content.");
                    }
                }

                // 3. HTML5 Audio / Video Auto-enhancement & Unmute on user gesture
                document.querySelectorAll('video, audio').forEach(function(el) {
                    el.setAttribute('playsinline', 'true');
                    el.setAttribute('webkit-playsinline', 'true');
                });
            } catch(e) {}
        })();
    """.trimIndent()
}
