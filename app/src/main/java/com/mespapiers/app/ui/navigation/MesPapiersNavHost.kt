package com.mespapiers.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mespapiers.app.ui.screens.dashboard.DashboardScreen
import com.mespapiers.app.ui.screens.document.AddDocumentScreen
import com.mespapiers.app.ui.screens.document.DocumentInfoScreen
import com.mespapiers.app.ui.screens.document.ScannerScreen
import com.mespapiers.app.ui.screens.export.ExportScreen
import com.mespapiers.app.ui.screens.onboarding.OnboardingScreen
import com.mespapiers.app.ui.screens.onboarding.OnboardingViewModel
import com.mespapiers.app.ui.screens.profile.ProfileCreateScreen
import com.mespapiers.app.ui.screens.profile.ProfilePickerScreen
import com.mespapiers.app.ui.screens.settings.SettingsScreen
import com.mespapiers.app.ui.screens.support.SupportScreen
import com.mespapiers.app.ui.screens.viewer.HistoryScreen
import com.mespapiers.app.ui.screens.viewer.ViewerScreen

private const val TRANSITION_DURATION = 300

@Composable
fun MesPapiersNavHost(
    navController: NavHostController = rememberNavController()
) {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val hasCompletedOnboarding by onboardingViewModel.hasCompletedOnboarding.collectAsState()
    val hasProfile by onboardingViewModel.hasProfile.collectAsState()

    // Use a stable start destination with loading state
    var isInitialized by remember { mutableStateOf(false) }
    var startDestination by remember { mutableStateOf(NavRoutes.Onboarding.route) }

    LaunchedEffect(hasCompletedOnboarding, hasProfile) {
        if (hasCompletedOnboarding != null) {
            startDestination = when {
                hasCompletedOnboarding == false -> NavRoutes.Onboarding.route
                hasProfile == false -> NavRoutes.ProfilePicker.route
                else -> NavRoutes.Dashboard.route
            }
            isInitialized = true
        }
    }

    // Show loading while determining initial destination
    if (!isInitialized) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))
        }
    ) {
        // Onboarding
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Profile
        composable(NavRoutes.ProfilePicker.route) {
            ProfilePickerScreen(
                onProfileSelected = {
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(NavRoutes.ProfilePicker.route) { inclusive = true }
                    }
                },
                onCreateProfile = {
                    navController.navigate(NavRoutes.ProfileCreate.route)
                }
            )
        }

        composable(NavRoutes.ProfileCreate.route) {
            ProfileCreateScreen(
                onProfileCreated = {
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(NavRoutes.ProfilePicker.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Dashboard
        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                onDocumentClick = { documentId ->
                    navController.navigate(NavRoutes.Viewer.createRoute(documentId))
                },
                onAddDocument = { categoryId ->
                    navController.navigate(NavRoutes.AddDocument.createRoute(categoryId))
                },
                onExportClick = {
                    navController.navigate(NavRoutes.Export.route)
                },
                onSettingsClick = {
                    navController.navigate(NavRoutes.Settings.route)
                },
                onSwitchProfile = {
                    navController.navigate(NavRoutes.ProfilePicker.route)
                }
            )
        }

        // Add Document
        composable(
            route = NavRoutes.AddDocument.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            AddDocumentScreen(
                categoryId = categoryId,
                onScanDocument = {
                    navController.navigate(NavRoutes.Scanner.createRoute(categoryId))
                },
                onImportComplete = { documentId ->
                    navController.navigate(NavRoutes.Viewer.createRoute(documentId)) {
                        popUpTo(NavRoutes.Dashboard.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Scanner
        composable(
            route = NavRoutes.Scanner.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            ScannerScreen(
                categoryId = categoryId,
                onScanComplete = { documentId ->
                    navController.navigate(NavRoutes.Viewer.createRoute(documentId)) {
                        popUpTo(NavRoutes.Dashboard.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Document Info
        composable(
            route = NavRoutes.DocumentInfo.route,
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            DocumentInfoScreen(
                documentId = documentId,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // Viewer
        composable(
            route = NavRoutes.Viewer.route,
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            ViewerScreen(
                documentId = documentId,
                onHistoryClick = {
                    navController.navigate(NavRoutes.History.createRoute(documentId))
                },
                onEditClick = {
                    navController.navigate(NavRoutes.DocumentInfo.createRoute(documentId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // History
        composable(
            route = NavRoutes.History.route,
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            HistoryScreen(
                documentId = documentId,
                onVersionClick = { versionId ->
                    navController.navigate(NavRoutes.VersionViewer.createRoute(versionId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Export
        composable(NavRoutes.Export.route) {
            ExportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Settings
        composable(NavRoutes.Settings.route) {
            SettingsScreen(
                onSupportClick = {
                    navController.navigate(NavRoutes.Support.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Support
        composable(NavRoutes.Support.route) {
            SupportScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
