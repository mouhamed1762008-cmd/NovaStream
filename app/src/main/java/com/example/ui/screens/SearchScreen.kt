package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaType
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MediaPosterCard
import com.example.ui.theme.NovaBorder
import com.example.ui.theme.NovaDarkBackground
import com.example.ui.theme.NovaPrimaryRed
import com.example.ui.theme.NovaSurfaceContainer
import com.example.ui.theme.NovaSurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VodViewModel

@Composable
fun SearchScreen(
    viewModel: VodViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val filterGenre by viewModel.searchFilterGenre.collectAsState()
    val filterType by viewModel.searchFilterType.collectAsState()
    val recommended by viewModel.recommended.collectAsState()

    val movieResults = results.filter { it.type == MediaType.MOVIE }
    val seriesResults = results.filter { it.type == MediaType.SERIES }

    val genres = listOf("All", "Sci-Fi", "Action", "Drama", "Mystery", "Cyberpunk", "Fantasy", "Crime")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NovaDarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Search Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(NovaSurfaceDark)
                    .testTag("search_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search movies, series, actors...", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = NovaPrimaryRed
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.setSearchQuery("") },
                            modifier = Modifier.testTag("search_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = TextMuted
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaPrimaryRed,
                    unfocusedBorderColor = NovaBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = NovaSurfaceContainer,
                    unfocusedContainerColor = NovaSurfaceContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field")
            )
        }

        // Filter Type Tabs (All / Movies / Series)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val types = listOf(
                Pair("All Types", null),
                Pair("Movies Only", MediaType.MOVIE),
                Pair("Series Only", MediaType.SERIES)
            )

            types.forEach { (label, type) ->
                val isSelected = filterType == type
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSearchFilterType(type) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NovaPrimaryRed,
                        selectedLabelColor = Color.White,
                        containerColor = NovaSurfaceContainer,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) NovaPrimaryRed else NovaBorder,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        // Genre Filter Chips Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                val isSelected = filterGenre == genre
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSearchFilterGenre(genre) },
                    label = { Text(genre, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2563EB),
                        selectedLabelColor = Color.White,
                        containerColor = NovaSurfaceContainer,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) Color(0xFF2563EB) else NovaBorder,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content Area
        if (query.isEmpty() && filterGenre == "All" && filterType == null) {
            // Empty Search State: Discover suggestions
            LazyVerticalGrid(
                columns = GridCells.Adaptive(110.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "Popular Searches & Suggestions",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try typing 'Astra', 'Cyberpunk', or tap a genre above",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }

                items(recommended, key = { "rec_${it.id}" }) { media ->
                    MediaPosterCard(
                        media = media,
                        onClick = { onNavigateToDetails(media.id) },
                        width = 110
                    )
                }
            }
        } else if (results.isEmpty()) {
            // No Results Found State
            EmptyStateView(
                icon = Icons.Outlined.SearchOff,
                title = "No Matches Found",
                message = "We couldn't find any title matching \"$query\". Check your spelling or browse genres.",
                actionButtonText = "Clear Filters",
                onActionClick = {
                    viewModel.setSearchQuery("")
                    viewModel.setSearchFilterGenre("All")
                    viewModel.setSearchFilterType(null)
                }
            )
        } else {
            // Search Results Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(115.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Found ${results.size} Titles",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                items(results, key = { it.id }) { media ->
                    MediaPosterCard(
                        media = media,
                        onClick = { onNavigateToDetails(media.id) },
                        width = 115
                    )
                }
            }
        }
    }
}
