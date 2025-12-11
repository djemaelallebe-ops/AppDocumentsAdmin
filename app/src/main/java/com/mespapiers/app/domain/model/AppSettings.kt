package com.mespapiers.app.domain.model

data class AppSettings(
    val biometricEnabled: Boolean = false,
    val tipSupportEnabled: Boolean = true,
    val externalDonationVisible: Boolean = true,
    val hasCompletedOnboarding: Boolean = false,
    val currentProfileId: String? = null
)
