package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Channel
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.UserSettings
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.VodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VodViewModel(
    private val vodRepository: VodRepository,
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {

    val userName: StateFlow<String> = userPrefsRepository.userNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val isOnboarded: StateFlow<Boolean?> = userPrefsRepository.isOnboardedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val favorites: StateFlow<Set<String>> = userPrefsRepository.favoritesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val watchHistory: StateFlow<List<String>> = userPrefsRepository.watchHistoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val continueWatching: StateFlow<List<ContinueWatchingItem>> = userPrefsRepository.continueWatchingFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings: StateFlow<UserSettings> = userPrefsRepository.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    // Catalog Flows
    val featuredContent: StateFlow<List<MediaItem>> = vodRepository.getFeaturedContent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trending: StateFlow<List<MediaItem>> = vodRepository.getTrending()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularMovies: StateFlow<List<MediaItem>> = vodRepository.getPopularMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularSeries: StateFlow<List<MediaItem>> = vodRepository.getPopularSeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recommended: StateFlow<List<MediaItem>> = vodRepository.getRecommended()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMovies: StateFlow<List<MediaItem>> = vodRepository.getAllMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSeries: StateFlow<List<MediaItem>> = vodRepository.getAllSeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveChannels: StateFlow<List<Channel>> = vodRepository.getLiveChannels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter selections
    val movieGenres = vodRepository.getMovieGenres()
    val seriesGenres = vodRepository.getSeriesGenres()
    val channelCategories = vodRepository.getChannelCategories()

    private val _selectedMovieGenre = MutableStateFlow("All")
    val selectedMovieGenre: StateFlow<String> = _selectedMovieGenre.asStateFlow()

    private val _selectedSeriesGenre = MutableStateFlow("All")
    val selectedSeriesGenre: StateFlow<String> = _selectedSeriesGenre.asStateFlow()

    private val _selectedChannelCategory = MutableStateFlow("All")
    val selectedChannelCategory: StateFlow<String> = _selectedChannelCategory.asStateFlow()

    // Filtered Movies
    val filteredMovies: StateFlow<List<MediaItem>> = combine(allMovies, _selectedMovieGenre) { movies, genre ->
        if (genre == "All") movies else movies.filter { it.genres.any { g -> g.equals(genre, ignoreCase = true) } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Series
    val filteredSeries: StateFlow<List<MediaItem>> = combine(allSeries, _selectedSeriesGenre) { series, genre ->
        if (genre == "All") series else series.filter { it.genres.any { g -> g.equals(genre, ignoreCase = true) } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Channels
    val filteredChannels: StateFlow<List<Channel>> = combine(liveChannels, _selectedChannelCategory) { channels, cat ->
        if (cat == "All") channels else channels.filter { it.category.equals(cat, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilterGenre = MutableStateFlow("All")
    val searchFilterGenre: StateFlow<String> = _searchFilterGenre.asStateFlow()

    private val _searchFilterType = MutableStateFlow<MediaType?>(null)
    val searchFilterType: StateFlow<MediaType?> = _searchFilterType.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    val searchResults: StateFlow<List<MediaItem>> = combine(
        _searchQuery,
        _searchFilterGenre,
        _searchFilterType
    ) { query, genre, type ->
        vodRepository.searchMedia(query, if (genre == "All") null else genre, type)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorite items full list
    val favoriteMediaItems: StateFlow<List<MediaItem>> = combine(favorites, recommended) { favIds, all ->
        all.filter { favIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // History items full list
    val historyMediaItems: StateFlow<List<MediaItem>> = combine(watchHistory, recommended) { histIds, all ->
        histIds.mapNotNull { id -> all.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveUserName(name: String) {
        viewModelScope.launch {
            userPrefsRepository.saveUserName(name.trim())
        }
    }

    fun toggleFavorite(mediaId: String) {
        viewModelScope.launch {
            userPrefsRepository.toggleFavorite(mediaId)
        }
    }

    fun recordWatchHistory(mediaId: String) {
        viewModelScope.launch {
            userPrefsRepository.addToHistory(mediaId)
        }
    }

    fun updateContinueWatching(
        media: MediaItem,
        currentPosMs: Long,
        totalDurationMs: Long,
        episodeTitle: String? = null,
        episodeId: String? = null,
        videoUrl: String = media.videoUrl
    ) {
        viewModelScope.launch {
            val subtitle = episodeTitle ?: "${media.releaseYear} • ${media.genres.firstOrNull() ?: ""}"
            userPrefsRepository.updateContinueWatching(
                ContinueWatchingItem(
                    mediaId = media.id,
                    title = media.title,
                    subtitle = subtitle,
                    posterUrl = media.posterUrl,
                    backdropUrl = media.backdropUrl,
                    localBackdropRes = media.localBackdropRes,
                    currentPositionMs = currentPosMs,
                    totalDurationMs = totalDurationMs,
                    videoUrl = videoUrl,
                    episodeId = episodeId
                )
            )
        }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            userPrefsRepository.updateSettings(settings)
        }
    }

    fun clearAllUserData() {
        viewModelScope.launch {
            userPrefsRepository.clearAllData()
        }
    }

    fun setMovieGenre(genre: String) {
        _selectedMovieGenre.value = genre
    }

    fun setSeriesGenre(genre: String) {
        _selectedSeriesGenre.value = genre
    }

    fun setChannelCategory(category: String) {
        _selectedChannelCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilterGenre(genre: String) {
        _searchFilterGenre.value = genre
    }

    fun setSearchFilterType(type: MediaType?) {
        _searchFilterType.value = type
    }

    fun getMediaById(id: String): MediaItem? = vodRepository.getMediaById(id)
    fun getChannelById(id: String): Channel? = vodRepository.getChannelById(id)

    class Factory(
        private val vodRepository: VodRepository,
        private val userPrefsRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VodViewModel::class.java)) {
                return VodViewModel(vodRepository, userPrefsRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
