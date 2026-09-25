package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.PrivacyIndigo
import com.example.ui.theme.PrivacyPurple
import com.example.ui.theme.ShieldEmerald

// Brand colors for social apps
val WhatsAppGreen = Color(0xFF25D366)
val TelegramBlue = Color(0xFF229ED9)
val ImoBlue = Color(0xFF1D8ECE)

const val DEVELOPER_NAME = "GM Ripon"
const val DEVELOPER_PHONE = "+8801911527072"
const val DEVELOPER_RAW_PHONE = "8801911527072"
const val DEVELOPER_EMAIL = "gmripon703@gmail.com"

/**
 * Helper to launch external URLs or app intents safely, with clipboard fallback.
 */
fun openContactUrl(context: Context, urlString: String, fallbackText: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        copyNumberToClipboard(context, fallbackText)
    }
}

/**
 * Helper to initiate a phone dialer intent safely.
 */
fun launchDialer(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        copyNumberToClipboard(context, phoneNumber)
    }
}

/**
 * Copies contact information to device clipboard with toast feedback.
 */
fun copyNumberToClipboard(context: Context, text: String, label: String = "GM Ripon Contact") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard: $text", Toast.LENGTH_SHORT).show()
}

/**
 * Reusable Developer Profile Card displayed in Settings, Home Screen, and About dialogs.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeveloperProfileCard(
    modifier: Modifier = Modifier,
    onShowFullDialog: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        ElectricCyan.copy(alpha = 0.5f),
                        PrivacyIndigo.copy(alpha = 0.4f),
                        PrivacyPurple.copy(alpha = 0.5f)
                    )
                ),
                RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Avatar, Name, and Verified Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ElectricCyan, PrivacyIndigo, PrivacyPurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GR",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Developed by $DEVELOPER_NAME",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified Developer",
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "Lead Developer & System Architect",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = DEVELOPER_PHONE,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onShowFullDialog != null) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { onShowFullDialog() }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.OpenInNew,
                            contentDescription = "About GM Ripon",
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Get in touch with GM Ripon for support, custom modules, or questions via WhatsApp, Telegram, or imo:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons in FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // WhatsApp Button
                SocialContactChip(
                    label = "WhatsApp",
                    subtitle = DEVELOPER_PHONE,
                    icon = Icons.Filled.Chat,
                    brandColor = WhatsAppGreen,
                    onClick = {
                        openContactUrl(
                            context = context,
                            urlString = "https://wa.me/$DEVELOPER_RAW_PHONE?text=Hello%20GM%20Ripon,%20I%20am%20using%20Browser%20RN",
                            fallbackText = DEVELOPER_PHONE
                        )
                    }
                )

                // Telegram Button
                SocialContactChip(
                    label = "Telegram",
                    subtitle = DEVELOPER_PHONE,
                    icon = Icons.Filled.Send,
                    brandColor = TelegramBlue,
                    onClick = {
                        openContactUrl(
                            context = context,
                            urlString = "https://t.me/+$DEVELOPER_RAW_PHONE",
                            fallbackText = DEVELOPER_PHONE
                        )
                    }
                )

                // imo Button
                SocialContactChip(
                    label = "imo",
                    subtitle = DEVELOPER_PHONE,
                    icon = Icons.Filled.Phone,
                    brandColor = ImoBlue,
                    onClick = {
                        // imo deep link with fallback to clipboard/dialer
                        openContactUrl(
                            context = context,
                            urlString = "https://imo.im",
                            fallbackText = DEVELOPER_PHONE
                        )
                    }
                )

                // Call / Dial Button
                SocialContactChip(
                    label = "Call",
                    subtitle = DEVELOPER_PHONE,
                    icon = Icons.Filled.Call,
                    brandColor = ShieldEmerald,
                    onClick = {
                        launchDialer(context, DEVELOPER_PHONE)
                    }
                )

                // Copy Number Chip
                SocialContactChip(
                    label = "Copy",
                    subtitle = "Clipboard",
                    icon = Icons.Filled.ContentCopy,
                    brandColor = ElectricCyan,
                    onClick = {
                        copyNumberToClipboard(context, DEVELOPER_PHONE)
                    }
                )

                // Email Chip
                SocialContactChip(
                    label = "Email",
                    subtitle = DEVELOPER_EMAIL,
                    icon = Icons.Filled.Email,
                    brandColor = PrivacyIndigo,
                    onClick = {
                        openContactUrl(
                            context = context,
                            urlString = "mailto:$DEVELOPER_EMAIL?subject=Browser%20RN%20App",
                            fallbackText = DEVELOPER_EMAIL
                        )
                    }
                )
            }
        }
    }
}

/**
 * Compact, colorful social contact chip for 1-tap contact.
 */
@Composable
fun SocialContactChip(
    label: String,
    subtitle: String,
    icon: ImageVector,
    brandColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = brandColor.copy(alpha = 0.14f),
        border = androidx.compose.foundation.BorderStroke(1.dp, brandColor.copy(alpha = 0.45f)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(brandColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }

            Spacer(modifier = Modifier.width(7.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = brandColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Full "About Developer" modal dialog with developer bio, full contact options, and versioning.
 */
@Composable
fun AboutDeveloperDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricCyan, PrivacyIndigo, PrivacyPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "About the Developer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Developed by $DEVELOPER_NAME",
                    style = MaterialTheme.typography.titleSmall,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Browser RN was developed by GM Ripon with a focus on high-speed browsing, zero-login WireGuard/DoH anonymous tunnels, unthrottled wire-speed performance, and built-in Flash Player & media support.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ContactRow(
                            label = "WhatsApp",
                            value = DEVELOPER_PHONE,
                            icon = Icons.Filled.Chat,
                            tint = WhatsAppGreen,
                            onClick = {
                                openContactUrl(
                                    context = context,
                                    urlString = "https://wa.me/$DEVELOPER_RAW_PHONE?text=Hello%20GM%20Ripon,%20I%20am%20using%20Browser%20RN",
                                    fallbackText = DEVELOPER_PHONE
                                )
                            }
                        )

                        ContactRow(
                            label = "Telegram",
                            value = DEVELOPER_PHONE,
                            icon = Icons.Filled.Send,
                            tint = TelegramBlue,
                            onClick = {
                                openContactUrl(
                                    context = context,
                                    urlString = "https://t.me/+$DEVELOPER_RAW_PHONE",
                                    fallbackText = DEVELOPER_PHONE
                                )
                            }
                        )

                        ContactRow(
                            label = "imo",
                            value = DEVELOPER_PHONE,
                            icon = Icons.Filled.Phone,
                            tint = ImoBlue,
                            onClick = {
                                openContactUrl(
                                    context = context,
                                    urlString = "https://imo.im",
                                    fallbackText = DEVELOPER_PHONE
                                )
                            }
                        )

                        ContactRow(
                            label = "Direct Call",
                            value = DEVELOPER_PHONE,
                            icon = Icons.Filled.Call,
                            tint = ShieldEmerald,
                            onClick = {
                                launchDialer(context, DEVELOPER_PHONE)
                            }
                        )

                        ContactRow(
                            label = "Email",
                            value = DEVELOPER_EMAIL,
                            icon = Icons.Filled.Email,
                            tint = PrivacyIndigo,
                            onClick = {
                                openContactUrl(
                                    context = context,
                                    urlString = "mailto:$DEVELOPER_EMAIL?subject=Browser%20RN%20Inquiry",
                                    fallbackText = DEVELOPER_EMAIL
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color.Black)
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    copyNumberToClipboard(context, DEVELOPER_PHONE)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy Number")
            }
        }
    )
}

@Composable
private fun ContactRow(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tint,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Icon(
            imageVector = Icons.Filled.OpenInNew,
            contentDescription = "Open $label",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )
    }
}
