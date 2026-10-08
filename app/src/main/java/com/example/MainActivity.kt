package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.VodRepositoryImpl
import com.example.ui.components.LoadingView
import com.example.ui.components.NovaBottomNav
import com.example.ui.navigation.Screen
import com.example.ui.screens.DetailsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveTvScreen
import com.example.ui.screens.MoviesScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SeriesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NovaDarkBackground
import com.example.ui.viewmodel.VodViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NovaStreamApp()
            }
        }
    }
}

@Composable
fun NovaStreamApp() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current

    val vodRepository = remember { VodRepositoryImpl() }
    val userPrefsRepository = remember { UserPreferencesRepository(context.applicationContext) }
    val viewModel: VodViewModel = viewModel(
        factory = VodViewModel.Factory(vodRepository, userPrefsRepository)
    )

    val isOnboarded by viewModel.isOnboarded.collectAsState()

    // Wait until preferences are read from disk
    if (isOnboarded == null) {
        LoadingView(message = "Starting NovaStream...")
        return
    }

    val startDestination = if (isOnboarded == true) Screen.Home.route else Screen.Onboarding.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Primary bottom bar routes
    val bottomBarRoutes = setOf(
        Screen.Home.route,
        Screen.Movies.route,
        Screen.Series.route,
        Screen.LiveTv.route,
        Screen.Profile.route
    )
    val shouldShowBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = NovaDarkBackground,
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NovaBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (shouldShowBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp.Hairline)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Enter Your Name / Onboarding Screen
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onContinue = { enteredName ->
                            viewModel.saveUserName(enteredName)
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }

                // 2. Home Screen
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { mediaId ->
                            navController.navigate(Screen.Details.createRoute(mediaId))
                        },
                        onNavigateToPlayer = { mediaId, channelId ->
                            navController.navigate(Screen.Player.createRoute(mediaId = mediaId, channelId = channelId))
                        },
                        onNavigateToSearch = {
                            navController.navigate(Screen.Search.route)
                        },
                        onNavigateToMovies = {
                            navController.navigate(Screen.Movies.route)
                        },
                        onNavigateToSeries = {
                            navController.navigate(Screen.Series.route)
                        },
                        onNavigateToLiveTv = {
                            navController.navigate(Screen.LiveTv.route)
                        }
                    )
                }

                // 3. Movies Screen
                composable(Screen.Movies.route) {
                    MoviesScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { mediaId ->
                            navController.navigate(Screen.Details.createRoute(mediaId))
                        },
                        onNavigateToPlayer = { mediaId ->
                            navController.navigate(Screen.Player.createRoute(mediaId = mediaId))
                        }
                    )
                }

                // 4. Series Screen
                composable(Screen.Series.route) {
                    SeriesScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { mediaId ->
                            navController.navigate(Screen.Details.createRoute(mediaId))
                        },
                        onNavigateToPlayer = { mediaId, episodeId ->
                            navController.navigate(Screen.Player.createRoute(mediaId = mediaId, episodeId = episodeId))
                        }
                    )
                }

                // 5. Live TV Screen
                composable(Screen.LiveTv.route) {
                    LiveTvScreen(
                        viewModel = viewModel,
                        onNavigateToPlayer = { channelId ->
                            navController.navigate(Screen.Player.createRoute(channelId = channelId))
                        }
                    )
                }

                // 6. Profile Screen
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { mediaId ->
                            navController.navigate(Screen.Details.createRoute(mediaId))
                        },
                        onNavigateToPlayer = { mediaId, channelId ->
                            navController.navigate(Screen.Player.createRoute(mediaId = mediaId, channelId = channelId))
                        },
                        onResetProfile = {
                            viewModel.clearAllUserData()
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // 7. Search Screen
                composable(Screen.Search.route) {
                    SearchScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDetails = { mediaId ->
                            navController.navigate(Screen.Details.createRoute(mediaId))
                        }
                    )
                }

                // 8. Details Screen
                composable(
                    route = Screen.Details.route,
                    arguments = listOf(
                        navArgument("mediaId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
                    DetailsScreen(
                        mediaId = mediaId,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToPlayer = { targetMediaId, episodeId ->
                            navController.navigate(Screen.Player.createRoute(mediaId = targetMediaId, episodeId = episodeId))
                        },
                        onNavigateToMedia = { newMediaId ->
                            navController.navigate(Screen.Details.createRoute(newMediaId))
                        }
                    )
                }

                // 9. Video Player Screen
                composable(
                    route = Screen.Player.route,
                    arguments = listOf(
                        navArgument("mediaId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("channelId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                        navArgument("episodeId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val mediaId = backStackEntry.arguments?.getString("mediaId")
                    val channelId = backStackEntry.arguments?.getString("channelId")
                    val episodeId = backStackEntry.arguments?.getString("episodeId")

                    PlayerScreen(
                        mediaId = mediaId,
                        channelId = channelId,
                        episodeId = episodeId,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
