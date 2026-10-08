package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "novastream_user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val FAVORITES = stringSetPreferencesKey("favorites")
        val WATCH_HISTORY = stringPreferencesKey("watch_history_json")
        val CONTINUE_WATCHING = stringPreferencesKey("continue_watching_json")
        val VIDEO_QUALITY = stringPreferencesKey("video_quality")
        val STREAM_WIFI_ONLY = booleanPreferencesKey("stream_wifi_only")
        val SUBTITLES_ENABLED = booleanPreferencesKey("subtitles_enabled")
        val AUTOPLAY_NEXT = booleanPreferencesKey("autoplay_next")
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.USER_NAME] ?: ""
    }

    val isOnboardedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.IS_ONBOARDED] ?: false
    }

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.FAVORITES] ?: emptySet()
    }

    val watchHistoryFlow: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[PreferencesKeys.WATCH_HISTORY] ?: "[]"
        try {
            val arr = JSONArray(raw)
            val list = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                list.add(arr.getString(i))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    val continueWatchingFlow: Flow<List<ContinueWatchingItem>> = context.dataStore.data.map { prefs ->
        val raw = prefs[PreferencesKeys.CONTINUE_WATCHING] ?: "[]"
        try {
            val arr = JSONArray(raw)
            val list = mutableListOf<ContinueWatchingItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ContinueWatchingItem(
                        mediaId = obj.getString("mediaId"),
                        title = obj.getString("title"),
                        subtitle = obj.optString("subtitle", ""),
                        posterUrl = obj.optString("posterUrl", ""),
                        backdropUrl = obj.optString("backdropUrl", ""),
                        currentPositionMs = obj.optLong("currentPositionMs", 0L),
                        totalDurationMs = obj.optLong("totalDurationMs", 0L),
                        videoUrl = obj.optString("videoUrl", ""),
                        episodeId = if (obj.has("episodeId") && !obj.isNull("episodeId")) obj.getString("episodeId") else null
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            videoQuality = prefs[PreferencesKeys.VIDEO_QUALITY] ?: "Auto (1080p)",
            streamOnWifiOnly = prefs[PreferencesKeys.STREAM_WIFI_ONLY] ?: false,
            subtitlesEnabled = prefs[PreferencesKeys.SUBTITLES_ENABLED] ?: true,
            autoPlayNext = prefs[PreferencesKeys.AUTOPLAY_NEXT] ?: true
        )
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.USER_NAME] = name
            prefs[PreferencesKeys.IS_ONBOARDED] = true
        }
    }

    suspend fun toggleFavorite(mediaId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.FAVORITES]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(mediaId)) {
                current.remove(mediaId)
            } else {
                current.add(mediaId)
            }
            prefs[PreferencesKeys.FAVORITES] = current
        }
    }

    suspend fun addToHistory(mediaId: String) {
        context.dataStore.edit { prefs ->
            val raw = prefs[PreferencesKeys.WATCH_HISTORY] ?: "[]"
            val list = try {
                val arr = JSONArray(raw)
                val items = mutableListOf<String>()
                for (i in 0 until arr.length()) {
                    items.add(arr.getString(i))
                }
                items
            } catch (_: Exception) {
                mutableListOf()
            }
            list.remove(mediaId)
            list.add(0, mediaId)
            val trimmed = list.take(30)
            val jsonArr = JSONArray()
            trimmed.forEach { jsonArr.put(it) }
            prefs[PreferencesKeys.WATCH_HISTORY] = jsonArr.toString()
        }
    }

    suspend fun updateContinueWatching(item: ContinueWatchingItem) {
        context.dataStore.edit { prefs ->
            val raw = prefs[PreferencesKeys.CONTINUE_WATCHING] ?: "[]"
            val list = try {
                val arr = JSONArray(raw)
                val items = mutableListOf<ContinueWatchingItem>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    items.add(
                        ContinueWatchingItem(
                            mediaId = obj.getString("mediaId"),
                            title = obj.getString("title"),
                            subtitle = obj.optString("subtitle", ""),
                            posterUrl = obj.optString("posterUrl", ""),
                            backdropUrl = obj.optString("backdropUrl", ""),
                            currentPositionMs = obj.optLong("currentPositionMs", 0L),
                            totalDurationMs = obj.optLong("totalDurationMs", 0L),
                            videoUrl = obj.optString("videoUrl", ""),
                            episodeId = if (obj.has("episodeId") && !obj.isNull("episodeId")) obj.getString("episodeId") else null
                        )
                    )
                }
                items
            } catch (_: Exception) {
                mutableListOf()
            }

            // Remove existing entry for same media or episode
            list.removeAll { it.mediaId == item.mediaId && it.episodeId == item.episodeId }
            list.add(0, item)
            val trimmed = list.take(15)

            val jsonArr = JSONArray()
            trimmed.forEach { itm ->
                val obj = JSONObject().apply {
                    put("mediaId", itm.mediaId)
                    put("title", itm.title)
                    put("subtitle", itm.subtitle)
                    put("posterUrl", itm.posterUrl)
                    put("backdropUrl", itm.backdropUrl)
                    put("currentPositionMs", itm.currentPositionMs)
                    put("totalDurationMs", itm.totalDurationMs)
                    put("videoUrl", itm.videoUrl)
                    if (itm.episodeId != null) put("episodeId", itm.episodeId)
                }
                jsonArr.put(obj)
            }
            prefs[PreferencesKeys.CONTINUE_WATCHING] = jsonArr.toString()
        }
    }

    suspend fun updateSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.VIDEO_QUALITY] = settings.videoQuality
            prefs[PreferencesKeys.STREAM_WIFI_ONLY] = settings.streamOnWifiOnly
            prefs[PreferencesKeys.SUBTITLES_ENABLED] = settings.subtitlesEnabled
            prefs[PreferencesKeys.AUTOPLAY_NEXT] = settings.autoPlayNext
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
