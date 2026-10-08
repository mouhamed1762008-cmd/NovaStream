package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.MediaItem
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.HeroBanner
import com.example.ui.components.LivePulseBadge
import com.example.ui.components.MediaRow
import com.example.ui.components.NovaTopAppBar
import com.example.ui.theme.NovaAccentCyan
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaDarkBackground
import com.example.ui.theme.NovaPrimaryRed
import com.example.ui.theme.NovaSurfaceContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.VodViewModel

@Composable
fun HomeScreen(
    viewModel: VodViewModel,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToPlayer: (mediaId: String?, channelId: String?) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMovies: () -> Unit,
    onNavigateToSeries: () -> Unit,
    onNavigateToLiveTv: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val featuredList by viewModel.featuredContent.collectAsState()
    val continueWatchingList by viewModel.continueWatching.collectAsState()
    val trendingList by viewModel.trending.collectAsState()
    val popularMoviesList by viewModel.popularMovies.collectAsState()
    val popularSeriesList by viewModel.popularSeries.collectAsState()
    val recommendedList by viewModel.recommended.collectAsState()
    val liveChannels by viewModel.liveChannels.collectAsState()

    val featuredItem = featuredList.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NovaDarkBackground)
    ) {
        NovaTopAppBar(
            userName = userName,
            onSearchClick = onNavigateToSearch
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Hero Banner
            if (featuredItem != null) {
                item(key = "hero") {
                    HeroBanner(
                        media = featuredItem,
                        onPlayClick = {
                            viewModel.recordWatchHistory(featuredItem.id)
                            onNavigateToPlayer(featuredItem.id, null)
                        },
                        onDetailsClick = { onNavigateToDetails(featuredItem.id) }
                    )
                }
            }

            // Continue Watching Section
            if (continueWatchingList.isNotEmpty()) {
                item(key = "continue_watching") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "Continue Watching",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(continueWatchingList, key = { it.mediaId + (it.episodeId ?: "") }) { item ->
                                ContinueWatchingCard(
                                    item = item,
                                    onClick = {
                                        onNavigateToPlayer(item.mediaId, null)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Live TV Shortcut Banner
            item(key = "live_tv_shortcut") {
                Surface(
                    color = NovaSurfaceContainer,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable(onClick = onNavigateToLiveTv)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        NovaPrimaryRed.copy(alpha = 0.15f),
                                        NovaSurfaceContainer
                                    )
                                )
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(NovaPrimaryRed.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LiveTv,
                                    contentDescription = "Live TV",
                                    tint = NovaPrimaryRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Live Channels",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    LivePulseBadge()
                                }
                                Text(
                                    text = "${liveChannels.size} Channels broadcasting right now",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Open Live TV",
                            tint = NovaAccentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Trending Content
            item(key = "trending") {
                MediaRow(
                    title = "Trending Now",
                    items = trendingList,
                    onItemClick = { onNavigateToDetails(it.id) }
                )
            }

            // Popular Movies
            item(key = "popular_movies") {
                MediaRow(
                    title = "Popular Movies",
                    items = popularMoviesList,
                    onItemClick = { onNavigateToDetails(it.id) },
                    onSeeAllClick = onNavigateToMovies
                )
            }

            // Popular Series
            item(key = "popular_series") {
                MediaRow(
                    title = "Top Bingeable Series",
                    items = popularSeriesList,
                    onItemClick = { onNavigateToDetails(it.id) },
                    onSeeAllClick = onNavigateToSeries
                )
            }

            // Recommended
            item(key = "recommended") {
                MediaRow(
                    title = "Recommended For You",
                    items = recommendedList,
                    onItemClick = { onNavigateToDetails(it.id) }
                )
            }
        }
    }
}
