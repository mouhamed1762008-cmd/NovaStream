package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Movies : Screen("movies")
    data object Series : Screen("series")
    data object LiveTv : Screen("livetv")
    data object Profile : Screen("profile")
    data object Search : Screen("search")
    data object Details : Screen("details/{mediaId}") {
        fun createRoute(mediaId: String) = "details/$mediaId"
    }
    data object Player : Screen("player?mediaId={mediaId}&channelId={channelId}&episodeId={episodeId}") {
        fun createRoute(mediaId: String? = null, channelId: String? = null, episodeId: String? = null): String {
            val params = mutableListOf<String>()
            if (mediaId != null) params.add("mediaId=$mediaId")
            if (channelId != null) params.add("channelId=$channelId")
            if (episodeId != null) params.add("episodeId=$episodeId")
            return if (params.isEmpty()) "player" else "player?" + params.joinToString("&")
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "bottom_nav_home"
    ),
    BottomNavItem(
        screen = Screen.Movies,
        title = "Movies",
        selectedIcon = Icons.Filled.Movie,
        unselectedIcon = Icons.Outlined.Movie,
        testTag = "bottom_nav_movies"
    ),
    BottomNavItem(
        screen = Screen.Series,
        title = "Series",
        selectedIcon = Icons.Filled.Tv,
        unselectedIcon = Icons.Outlined.Tv,
        testTag = "bottom_nav_series"
    ),
    BottomNavItem(
        screen = Screen.LiveTv,
        title = "Live TV",
        selectedIcon = Icons.Filled.LiveTv,
        unselectedIcon = Icons.Outlined.LiveTv,
        testTag = "bottom_nav_livetv"
    ),
    BottomNavItem(
        screen = Screen.Profile,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "bottom_nav_profile"
    )
)
