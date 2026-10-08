package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.components.MediaRow
import com.example.ui.theme.NovaAccentCyan
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaDarkBackground
import com.example.ui.theme.NovaGoldRating
import com.example.ui.theme.NovaPrimaryRed
import com.example.ui.theme.NovaSurfaceContainer
import com.example.ui.theme.NovaSurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VodViewModel

@Composable
fun DetailsScreen(
    mediaId: String,
    viewModel: VodViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (mediaId: String?, episodeId: String?) -> Unit,
    onNavigateToMedia: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val media = remember(mediaId) { viewModel.getMediaById(mediaId) }
    val favorites by viewModel.favorites.collectAsState()
    val recommended by viewModel.recommended.collectAsState()

    if (media == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NovaDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Content not found", color = TextPrimary)
        }
        return
    }

    val isFavorite = favorites.contains(media.id)
    var selectedSeasonIndex by remember { mutableStateOf(0) }
    val relatedItems = remember(mediaId, recommended) {
        recommended.filter { it.id != mediaId && it.genres.any { g -> media.genres.contains(g) } }.take(8)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NovaDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Hero Backdrop Image Section
            item(key = "backdrop") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    if (media.localBackdropRes != null) {
                        Image(
                            painter = painterResource(id = media.localBackdropRes),
                            contentDescription = media.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = media.backdropUrl.ifEmpty { media.posterUrl },
                            contentDescription = media.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        NovaDarkBackground.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        NovaDarkBackground.copy(alpha = 0.8f),
                                        NovaDarkBackground
                                    )
                                )
                            )
                    )

                    // Big Play button floating on backdrop
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(NovaPrimaryRed.copy(alpha = 0.9f))
                            .clickable {
                                viewModel.recordWatchHistory(media.id)
                                onNavigateToPlayer(media.id, null)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Info & Meta Section
            item(key = "info") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Title
                    Text(
                        text = media.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Badges row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = NovaGoldRating,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "%.1f".format(media.rating),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            color = NovaSurfaceContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${media.releaseYear}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            color = NovaSurfaceContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = media.durationOrSeasons,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            color = NovaSurfaceContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = media.ageRating,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            color = NovaAccentCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "4K UHD",
                                color = NovaAccentCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Genre Pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(media.genres) { genre ->
                            Surface(
                                color = NovaSurfaceDark,
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder)
                            ) {
                                Text(
                                    text = genre,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons (Play Now, Watchlist, Share)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.recordWatchHistory(media.id)
                                onNavigateToPlayer(media.id, null)
                            },
                            modifier = Modifier
                                .weight(1.8f)
                                .height(48.dp)
                                .testTag("details_play_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NovaPrimaryRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Now", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { viewModel.toggleFavorite(media.id) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("details_favorite_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isFavorite) NovaPrimaryRed.copy(alpha = 0.2f) else NovaSurfaceContainer
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isFavorite) NovaPrimaryRed else NovaBorder
                            )
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Watchlist",
                                tint = if (isFavorite) NovaPrimaryRed else TextPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out \"${media.title}\" on NovaStream!")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share with friends")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("details_share_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = NovaSurfaceContainer),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = "Share", tint = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Overview
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = media.overview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    // Cast list
                    if (media.cast.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Cast: ${media.cast.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }

            // Series Season & Episode Selection (if Series)
            if (media.type == MediaType.SERIES && media.seasons.isNotEmpty()) {
                item(key = "seasons_header") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = "Episodes",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Season selector tabs
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(media.seasons.indices.toList()) { index ->
                                val season = media.seasons[index]
                                val isSelected = selectedSeasonIndex == index
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedSeasonIndex = index },
                                    label = { Text("Season ${season.seasonNumber}") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NovaPrimaryRed,
                                        selectedLabelColor = Color.White,
                                        containerColor = NovaSurfaceContainer,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                val currentSeason = media.seasons.getOrNull(selectedSeasonIndex) ?: media.seasons.first()
                items(currentSeason.episodes, key = { it.id }) { episode ->
                    EpisodeListItem(
                        episode = episode,
                        onClick = {
                            viewModel.recordWatchHistory(media.id)
                            viewModel.updateContinueWatching(
                                media = media,
                                currentPosMs = 0L,
                                totalDurationMs = 2800000L,
                                episodeTitle = "S${episode.seasonNumber}:E${episode.episodeNumber} • ${episode.title}",
                                episodeId = episode.id,
                                videoUrl = episode.videoUrl
                            )
                            onNavigateToPlayer(media.id, episode.id)
                        }
                    )
                }
            }

            // Related Content
            if (relatedItems.isNotEmpty()) {
                item(key = "related") {
                    Spacer(modifier = Modifier.height(16.dp))
                    MediaRow(
                        title = "More Like This",
                        items = relatedItems,
                        onItemClick = { onNavigateToMedia(it.id) }
                    )
                }
            }
        }

        // Top Back Button Floating
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(12.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .border(1.dp, NovaBorder, CircleShape)
                .align(Alignment.TopStart)
                .testTag("details_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

@Composable
fun EpisodeListItem(
    episode: Episode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NovaSurfaceDark,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Episode Thumbnail with Play Overlay
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NovaSurfaceContainer)
            ) {
                AsyncImage(
                    model = episode.thumbnailUrl,
                    contentDescription = episode.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play Episode",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${episode.episodeNumber}. ${episode.title}",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = episode.duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaAccentCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = episode.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
