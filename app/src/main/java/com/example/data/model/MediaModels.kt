package com.example.data.model

enum class MediaType {
    MOVIE,
    SERIES,
    LIVE
}

data class MediaItem(
    val id: String,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val localBackdropRes: Int? = null,
    val rating: Double,
    val releaseYear: Int,
    val durationOrSeasons: String,
    val ageRating: String,
    val genres: List<String>,
    val type: MediaType,
    val videoUrl: String,
    val cast: List<String> = emptyList(),
    val director: String = "",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val seasons: List<Season> = emptyList()
)

data class Season(
    val seasonNumber: Int,
    val title: String,
    val episodes: List<Episode>
)

data class Episode(
    val id: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val title: String,
    val overview: String,
    val duration: String,
    val thumbnailUrl: String,
    val videoUrl: String
)

data class Channel(
    val id: String,
    val channelNumber: Int,
    val name: String,
    val category: String,
    val logoUrl: String,
    val currentShow: String,
    val showProgress: Float,
    val showTime: String,
    val nextShow: String,
    val streamUrl: String
)

data class ContinueWatchingItem(
    val mediaId: String,
    val title: String,
    val subtitle: String,
    val posterUrl: String,
    val backdropUrl: String,
    val localBackdropRes: Int? = null,
    val currentPositionMs: Long,
    val totalDurationMs: Long,
    val videoUrl: String,
    val episodeId: String? = null
) {
    val progress: Float
        get() = if (totalDurationMs > 0) (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

data class UserSettings(
    val videoQuality: String = "Auto (1080p)",
    val streamOnWifiOnly: Boolean = false,
    val subtitlesEnabled: Boolean = true,
    val autoPlayNext: Boolean = true
)
