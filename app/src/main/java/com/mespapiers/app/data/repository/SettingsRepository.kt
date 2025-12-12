package com.mespapiers.app.data.repository

import com.mespapiers.app.data.local.dao.AppSettingsDao
import com.mespapiers.app.data.local.dao.AppStatsDao
import com.mespapiers.app.data.local.entity.AppSettingsEntity
import com.mespapiers.app.data.local.entity.AppStatsEntity
import com.mespapiers.app.domain.model.AppSettings
import com.mespapiers.app.domain.model.AppStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val appSettingsDao: AppSettingsDao,
    private val appStatsDao: AppStatsDao
) {
    // Settings
    fun getSettings(): Flow<AppSettings> {
        return appSettingsDao.getSettings().map { entity ->
            entity?.toDomain() ?: AppSettings()
        }
    }

    suspend fun getSettingsSync(): AppSettings {
        return appSettingsDao.getSettingsSync()?.toDomain() ?: AppSettings()
    }

    suspend fun initializeSettings() {
        if (appSettingsDao.getSettingsSync() == null) {
            appSettingsDao.insertSettings(AppSettingsEntity())
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        ensureSettingsExist()
        appSettingsDao.setOnboardingCompleted(completed)
    }

    suspend fun hasCompletedOnboarding(): Boolean {
        return appSettingsDao.hasCompletedOnboarding() ?: false
    }

    suspend fun setCurrentProfileId(profileId: String?) {
        ensureSettingsExist()
        appSettingsDao.setCurrentProfileId(profileId)
    }

    suspend fun getCurrentProfileId(): String? {
        return appSettingsDao.getCurrentProfileId()
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        ensureSettingsExist()
        appSettingsDao.setBiometricEnabled(enabled)
    }

    private suspend fun ensureSettingsExist() {
        if (appSettingsDao.getSettingsSync() == null) {
            appSettingsDao.insertSettings(AppSettingsEntity())
        }
    }

    // Stats
    fun getStats(): Flow<AppStats> {
        return appStatsDao.getStats().map { entity ->
            entity?.toDomain() ?: AppStats()
        }
    }

    suspend fun getStatsSync(): AppStats {
        return appStatsDao.getStatsSync()?.toDomain() ?: AppStats()
    }

    suspend fun initializeStats() {
        if (appStatsDao.getStatsSync() == null) {
            appStatsDao.insertStats(AppStatsEntity())
        }
    }

    suspend fun incrementOpenCount() {
        ensureStatsExist()
        appStatsDao.incrementOpenCount()
    }

    suspend fun recordSupportPromptShown() {
        ensureStatsExist()
        appStatsDao.setLastSupportPromptAt(System.currentTimeMillis())
    }

    suspend fun recordRatingPromptShown() {
        ensureStatsExist()
        appStatsDao.setLastRatingPromptAt(System.currentTimeMillis())
    }

    suspend fun setHasRated(hasRated: Boolean) {
        ensureStatsExist()
        appStatsDao.setHasRated(hasRated)
    }

    suspend fun setRatingPromptDisabled(disabled: Boolean) {
        ensureStatsExist()
        appStatsDao.setRatingPromptDisabled(disabled)
    }

    suspend fun setSupportPromptDisabled(disabled: Boolean) {
        ensureStatsExist()
        appStatsDao.setSupportPromptDisabled(disabled)
    }

    private suspend fun ensureStatsExist() {
        if (appStatsDao.getStatsSync() == null) {
            appStatsDao.insertStats(AppStatsEntity())
        }
    }
}
