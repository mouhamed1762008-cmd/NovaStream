package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.MediaPosterCard
import com.example.ui.theme.NovaAccentCyan
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaDarkBackground
import com.example.ui.theme.NovaLiveRed
import com.example.ui.theme.NovaPrimaryRed
import com.example.ui.theme.NovaSurfaceContainer
import com.example.ui.theme.NovaSurfaceDark
import com.example.ui.theme.NovaSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VodViewModel

@Composable
fun ProfileScreen(
    viewModel: VodViewModel,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToPlayer: (mediaId: String?, channelId: String?) -> Unit,
    onResetProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsState()
    val favorites by viewModel.favoriteMediaItems.collectAsState()
    val history by viewModel.historyMediaItems.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val settings by viewModel.userSettings.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editNameText by remember { mutableStateOf("") }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NovaDarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile Card Header
            item(key = "profile_header") {
                Surface(
                    color = NovaSurfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF1E283E),
                                        Color(0xFF151C2C)
                                    )
                                )
                            )
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // User Avatar with glowing border
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(NovaPrimaryRed)
                                    .border(2.dp, NovaAccentCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.firstOrNull()?.uppercase() ?: "U",
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = userName.ifEmpty { "Guest Streamer" },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Premium 4K Member",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NovaAccentCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                editNameText = userName
                                showEditNameDialog = true
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NovaSurfaceVariant)
                                .testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Continue Watching Section
            if (continueWatching.isNotEmpty()) {
                item(key = "profile_continue_watching") {
                    Column {
                        Text(
                            text = "Continue Watching",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(continueWatching, key = { it.mediaId }) { item ->
                                ContinueWatchingCard(
                                    item = item,
                                    onClick = { onNavigateToPlayer(item.mediaId, null) }
                                )
                            }
                        }
                    }
                }
            }

            // Favorites / Watchlist Section
            item(key = "profile_favorites") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = NovaPrimaryRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "My Watchlist (${favorites.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (favorites.isEmpty()) {
                        Surface(
                            color = NovaSurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Your watchlist is empty. Tap the heart or plus icon on any movie or series to save it here.",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(favorites, key = { "fav_${it.id}" }) { media ->
                                MediaPosterCard(
                                    media = media,
                                    onClick = { onNavigateToDetails(media.id) },
                                    width = 120
                                )
                            }
                        }
                    }
                }
            }

            // Watch History Section
            item(key = "profile_history") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = NovaAccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Watch History (${history.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (history.isEmpty()) {
                        Surface(
                            color = NovaSurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No watch history yet. Start streaming your favorite shows!",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(history, key = { "hist_${it.id}" }) { media ->
                                MediaPosterCard(
                                    media = media,
                                    onClick = { onNavigateToDetails(media.id) },
                                    width = 120
                                )
                            }
                        }
                    }
                }
            }

            // App Settings Section
            item(key = "profile_settings") {
                Column {
                    Text(
                        text = "App Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = NovaSurfaceDark,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                            // Quality toggle
                            SettingsRow(
                                icon = Icons.Filled.HighQuality,
                                title = "Streaming Quality",
                                subtitle = settings.videoQuality,
                                onClick = {
                                    val next = when (settings.videoQuality) {
                                        "Auto (1080p)" -> "Ultra HD (4K HDR)"
                                        "Ultra HD (4K HDR)" -> "Data Saver (720p)"
                                        else -> "Auto (1080p)"
                                    }
                                    viewModel.updateSettings(settings.copy(videoQuality = next))
                                    Toast.makeText(context, "Streaming quality: $next", Toast.LENGTH_SHORT).show()
                                }
                            )

                            HorizontalDivider(color = NovaBorder.copy(alpha = 0.5f))

                            // Wi-Fi Only Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Wifi, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Stream on Wi-Fi Only", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Text("Prevent cellular data consumption", color = TextMuted, fontSize = 11.sp)
                                    }
                                }
                                Switch(
                                    checked = settings.streamOnWifiOnly,
                                    onCheckedChange = {
                                        viewModel.updateSettings(settings.copy(streamOnWifiOnly = it))
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NovaPrimaryRed)
                                )
                            }

                            HorizontalDivider(color = NovaBorder.copy(alpha = 0.5f))

                            // Subtitles Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Subtitles, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Subtitles / Closed Captions", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Text("Enable automatic subtitles on player", color = TextMuted, fontSize = 11.sp)
                                    }
                                }
                                Switch(
                                    checked = settings.subtitlesEnabled,
                                    onCheckedChange = {
                                        viewModel.updateSettings(settings.copy(subtitlesEnabled = it))
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NovaPrimaryRed)
                                )
                            }
                        }
                    }
                }
            }

            // About & System Info
            item(key = "profile_about") {
                Surface(
                    color = NovaSurfaceDark,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = NovaAccentCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("About NovaStream", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "NovaStream v1.0.0 (Build 36) • High-Performance VOD Platform\nDesigned exclusively for modern Android smartphones & tablets.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Account Actions (Clear Data / Logout)
            item(key = "profile_actions") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { showClearDataDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("clear_data_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NovaLiveRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NovaLiveRed.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Watch History & Cache", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onResetProfile,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("logout_profile_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NovaSurfaceContainer)
                    ) {
                        Icon(Icons.Filled.Logout, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch Profile / Log Out", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Display Name", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editNameText,
                    onValueChange = { editNameText = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NovaPrimaryRed,
                        unfocusedBorderColor = NovaBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = editNameText.trim()
                        if (trimmed.isNotEmpty()) {
                            viewModel.saveUserName(trimmed)
                            showEditNameDialog = false
                            Toast.makeText(context, "Name updated to $trimmed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaPrimaryRed)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = NovaSurfaceContainer
        )
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Data?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text("This will delete your favorites, watch history, and continue watching progress. This cannot be undone.", color = TextSecondary)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllUserData()
                        showClearDataDialog = false
                        Toast.makeText(context, "All local stream data cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaLiveRed)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = NovaSurfaceContainer
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(subtitle, color = NovaAccentCyan, fontSize = 12.sp)
            }
        }
        Text("Change", color = TextMuted, fontSize = 12.sp)
    }
}
