package com.mespapiers.app.ui.navigation

sealed class NavRoutes(val route: String) {
    // Onboarding
    data object Onboarding : NavRoutes("onboarding")

    // Profile
    data object ProfilePicker : NavRoutes("profile_picker")
    data object ProfileCreate : NavRoutes("profile_create")
    data object ProfileEdit : NavRoutes("profile_edit/{profileId}") {
        fun createRoute(profileId: String) = "profile_edit/$profileId"
    }

    // Dashboard
    data object Dashboard : NavRoutes("dashboard")

    // Document
    data object AddDocument : NavRoutes("add_document/{categoryId}") {
        fun createRoute(categoryId: String) = "add_document/$categoryId"
    }
    data object DocumentInfo : NavRoutes("document_info/{documentId}") {
        fun createRoute(documentId: String) = "document_info/$documentId"
    }
    data object Scanner : NavRoutes("scanner/{categoryId}") {
        fun createRoute(categoryId: String) = "scanner/$categoryId"
    }

    // Viewer
    data object Viewer : NavRoutes("viewer/{documentId}") {
        fun createRoute(documentId: String) = "viewer/$documentId"
    }
    data object VersionViewer : NavRoutes("version_viewer/{versionId}") {
        fun createRoute(versionId: String) = "version_viewer/$versionId"
    }
    data object History : NavRoutes("history/{documentId}") {
        fun createRoute(documentId: String) = "history/$documentId"
    }

    // Export
    data object Export : NavRoutes("export")

    // Settings
    data object Settings : NavRoutes("settings")
    data object Support : NavRoutes("support")
}
