package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HeroBanner
import com.example.ui.components.MediaPosterCard
import com.example.ui.components.MediaRow
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaDarkBackground
import com.example.ui.theme.NovaPrimaryRed
import com.example.ui.theme.NovaSurfaceContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VodViewModel

@Composable
fun SeriesScreen(
    viewModel: VodViewModel,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToPlayer: (mediaId: String?, episodeId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val allSeries by viewModel.filteredSeries.collectAsState()
    val popularSeries by viewModel.popularSeries.collectAsState()
    val selectedGenre by viewModel.selectedSeriesGenre.collectAsState()
    val genres = viewModel.seriesGenres

    val featuredSeries = popularSeries.firstOrNull() ?: allSeries.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NovaDarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Series Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "TV Shows & Series",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Binge full seasons, premier releases, and award-winning originals",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }

        // Genre filter row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(genres) { genre ->
                val isSelected = selectedGenre == genre
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSeriesGenre(genre) },
                    label = { Text(genre, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF6366F1),
                        selectedLabelColor = Color.White,
                        containerColor = NovaSurfaceContainer,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) Color(0xFF6366F1) else NovaBorder,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 90.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Featured Series Banner if "All" is selected
            if (selectedGenre == "All" && featuredSeries != null) {
                item(key = "featured_series") {
                    HeroBanner(
                        media = featuredSeries,
                        onPlayClick = {
                            viewModel.recordWatchHistory(featuredSeries.id)
                            onNavigateToPlayer(featuredSeries.id, null)
                        },
                        onDetailsClick = { onNavigateToDetails(featuredSeries.id) }
                    )
                }

                item(key = "trending_series_row") {
                    MediaRow(
                        title = "Trending Dramas & Sci-Fi",
                        items = popularSeries,
                        onItemClick = { onNavigateToDetails(it.id) }
                    )
                }
            }

            // Grid / Collection of series
            item(key = "all_series_header") {
                Text(
                    text = if (selectedGenre == "All") "Browse All Series" else "$selectedGenre Series",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            item(key = "series_grid") {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(115.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((allSeries.size / 3 + 1) * 220).coerceAtLeast(300).dp)
                ) {
                    items(allSeries, key = { it.id }) { series ->
                        MediaPosterCard(
                            media = series,
                            onClick = { onNavigateToDetails(series.id) },
                            width = 115
                        )
                    }
                }
            }
        }
    }
}
