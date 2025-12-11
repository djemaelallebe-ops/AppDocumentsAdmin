package com.mespapiers.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: String = "GLOBAL_SETTINGS",

    @ColumnInfo(name = "biometric_enabled")
    val biometricEnabled: Boolean = false,

    @ColumnInfo(name = "tip_support_enabled")
    val tipSupportEnabled: Boolean = true,

    @ColumnInfo(name = "external_donation_visible")
    val externalDonationVisible: Boolean = true,

    @ColumnInfo(name = "has_completed_onboarding")
    val hasCompletedOnboarding: Boolean = false,

    @ColumnInfo(name = "current_profile_id")
    val currentProfileId: String? = null
)
