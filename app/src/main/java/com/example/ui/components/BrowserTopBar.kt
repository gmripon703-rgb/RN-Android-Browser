package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.BrowserTab
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ShieldEmerald

@Composable
fun BrowserTopBar(
    tab: BrowserTab?,
    tabCount: Int,
    shieldEnabled: Boolean,
    isBookmarked: Boolean,
    onNavigate: (String) -> Unit,
    onReload: () -> Unit,
    onStop: () -> Unit,
    onOpenShield: () -> Unit,
    onOpenTabs: () -> Unit,
    onEraseSession: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenVpnHub: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleTvMode: () -> Unit,
    isDesktopMode: Boolean,
    onToggleDesktopMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(tab?.url) {
        if (!isEditing) {
            urlInput = if (tab?.url == "about:home") "" else (tab?.url ?: "")
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shield Button with Live Block Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onOpenShield() }
                    .padding(4.dp)
                    .testTag("shield_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Privacy Shield",
                    tint = if (shieldEnabled) ShieldEmerald else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )

                if (shieldEnabled && (tab?.blockedCount ?: 0) > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${tab?.blockedCount?.coerceAtMost(99)}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Omnibox (Search / URL bar)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .border(
                        width = if (isEditing) 1.5.dp else 0.5.dp,
                        color = if (isEditing) ElectricCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lock icon if secure HTTPS
                    if (!isEditing && tab?.isSecureHttps == true && tab.url != "about:home") {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Secure HTTPS",
                            tint = ShieldEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = !isEditing) {
                                isEditing = true
                                urlInput = if (tab?.url == "about:home") "" else (tab?.url ?: "")
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (isEditing) {
                            BasicTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                ),
                                cursorBrush = SolidColor(ElectricCyan),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                keyboardActions = KeyboardActions(onGo = {
                                    focusManager.clearFocus()
                                    isEditing = false
                                    if (urlInput.isNotBlank()) onNavigate(urlInput)
                                }),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                                    .testTag("omnibox_input")
                            )
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        } else {
                            Text(
                                text = when {
                                    tab?.url == "about:home" -> "Search or enter address"
                                    else -> tab?.url ?: "Search or enter address"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (tab?.url == "about:home")
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Clear / Reload / Stop action
                    if (isEditing) {
                        if (urlInput.isNotEmpty()) {
                            IconButton(
                                onClick = { urlInput = "" },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        if (tab?.isLoading == true) {
                            IconButton(
                                onClick = onStop,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Stop",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else if (tab?.url != "about:home") {
                            IconButton(
                                onClick = onReload,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Reload",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Stealth Erase Flame Button
            IconButton(
                onClick = onEraseSession,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("erase_session_btn")
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "Erase Session",
                    tint = Color(0xFFF43F5E),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Tab Switcher Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
                modifier = Modifier
                    .clickable { onOpenTabs() }
                    .testTag("tabs_pill")
            ) {
                Text(
                    text = "$tabCount",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = ElectricCyan
                )
            }

            // Overflow Menu
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("overflow_menu_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Options",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(if (isBookmarked) "Remove Bookmark" else "Add to Bookmarks")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleBookmark()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Saved Bookmarks") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenBookmarks()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("VPN & Encrypted DNS") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.VpnLock,
                                contentDescription = null,
                                tint = ShieldEmerald
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenVpnHub()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isDesktopMode) "Switch to Mobile View" else "Request Desktop Site") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Devices,
                                contentDescription = null,
                                tint = if (isDesktopMode) ElectricCyan else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleDesktopMode()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Toggle TV Remote Mode") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Tv,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleTvMode()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Settings & Privacy") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenSettings()
                        }
                    )
                }
            }
        }

        // Animated neon loading progress bar
        AnimatedVisibility(
            visible = tab?.isLoading == true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(
                progress = { ((tab?.progress ?: 0) / 100f).coerceIn(0.05f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = ElectricCyan,
                trackColor = Color.Transparent
            )
        }
    }
}
