package com.example.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Javascript
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.BrowserTab
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ShieldEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShieldSheet(
    tab: BrowserTab?,
    shieldEnabled: Boolean,
    blockThirdPartyCookies: Boolean,
    webrtcLeakProtection: Boolean,
    flashPlayerEnabled: Boolean,
    blockImages: Boolean,
    javascriptEnabled: Boolean,
    onToggleShield: () -> Unit,
    onToggleCookies: () -> Unit,
    onToggleWebRtc: () -> Unit,
    onToggleFlash: () -> Unit,
    onToggleImages: () -> Unit,
    onToggleJavascript: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (shieldEnabled) ShieldEmerald.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Shield Hub",
                            tint = if (shieldEnabled) ShieldEmerald else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Privacy Shield",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (shieldEnabled) "Active Protection • Zero Telemetry" else "Protection Paused",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (shieldEnabled) ShieldEmerald else Color.Gray
                        )
                    }
                }
            }

            // Site Domain & Security Status
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Current Site",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = tab?.url ?: "about:home",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (tab?.isSecureHttps == true) ShieldEmerald.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (tab?.isSecureHttps == true) Icons.Filled.Lock else Icons.Filled.Block,
                                        contentDescription = null,
                                        tint = if (tab?.isSecureHttps == true) ShieldEmerald else Color.Red,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (tab?.isSecureHttps == true) "Secure HTTPS" else "Insecure",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (tab?.isSecureHttps == true) ShieldEmerald else Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Blocked on this page counter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Trackers Blocked on this page",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${tab?.blockedCount ?: 0}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = ElectricCyan
                            )
                        }
                    }
                }
            }

            // Toggles
            item {
                Text(
                    text = "Protection Controls",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ShieldToggleItem(
                    icon = Icons.Filled.Shield,
                    title = "Enhanced Tracker Blocking",
                    description = "Blocks cross-site ad networks, invasive telemetry & scripts",
                    checked = shieldEnabled,
                    onCheckedChange = { onToggleShield() },
                    tag = "toggle_shield"
                )
            }

            item {
                ShieldToggleItem(
                    icon = Icons.Filled.Cookie,
                    title = "Block 3rd-Party Cookies",
                    description = "Prevents sites from tracking your identity across the web",
                    checked = blockThirdPartyCookies,
                    onCheckedChange = { onToggleCookies() },
                    tag = "toggle_cookies"
                )
            }

            item {
                ShieldToggleItem(
                    icon = Icons.Filled.Security,
                    title = "Prevent WebRTC IP Leaks",
                    description = "Hides your real local IP address from malicious web pages",
                    checked = webrtcLeakProtection,
                    onCheckedChange = { onToggleWebRtc() },
                    tag = "toggle_webrtc"
                )
            }

            item {
                ShieldToggleItem(
                    icon = Icons.Filled.PlayCircle,
                    title = "Flash Player & Media Engine",
                    description = "Runs legacy Flash movies, animations & games via WebAssembly (Ruffle)",
                    checked = flashPlayerEnabled,
                    onCheckedChange = { onToggleFlash() },
                    tag = "toggle_flash"
                )
            }

            item {
                ShieldToggleItem(
                    icon = Icons.Filled.Photo,
                    title = "Block Images (Data Saver)",
                    description = "Stops image downloads for ultra-fast and stealthy browsing",
                    checked = blockImages,
                    onCheckedChange = { onToggleImages() },
                    tag = "toggle_images"
                )
            }

            item {
                ShieldToggleItem(
                    icon = Icons.Filled.Javascript,
                    title = "JavaScript Execution",
                    description = "Toggle JS execution for extreme hardening against exploits",
                    checked = javascriptEnabled,
                    onCheckedChange = { onToggleJavascript() },
                    tag = "toggle_js"
                )
            }

            // Blocked Items Breakdown
            if ((tab?.blockedList?.size ?: 0) > 0) {
                item {
                    Text(
                        text = "Blocked Domains (${tab?.blockedList?.size})",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(tab?.blockedList.orEmpty()) { tracker ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(tracker.category.colorHex))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tracker.domain,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = tracker.category.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(tracker.category.colorHex)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ShieldToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ElectricCyan,
                    checkedTrackColor = ElectricCyan.copy(alpha = 0.3f)
                ),
                modifier = Modifier.testTag(tag)
            )
        }
    }
}
