package com.mespapiers.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mespapiers.app.data.local.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {

    @Query("SELECT * FROM app_settings WHERE id = 'GLOBAL_SETTINGS'")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 'GLOBAL_SETTINGS'")
    suspend fun getSettingsSync(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettingsEntity)

    @Update
    suspend fun updateSettings(settings: AppSettingsEntity)

    @Query("UPDATE app_settings SET has_completed_onboarding = :completed WHERE id = 'GLOBAL_SETTINGS'")
    suspend fun setOnboardingCompleted(completed: Boolean)

    @Query("UPDATE app_settings SET current_profile_id = :profileId WHERE id = 'GLOBAL_SETTINGS'")
    suspend fun setCurrentProfileId(profileId: String?)

    @Query("UPDATE app_settings SET biometric_enabled = :enabled WHERE id = 'GLOBAL_SETTINGS'")
    suspend fun setBiometricEnabled(enabled: Boolean)

    @Query("SELECT current_profile_id FROM app_settings WHERE id = 'GLOBAL_SETTINGS'")
    suspend fun getCurrentProfileId(): String?

    @Query("SELECT has_completed_onboarding FROM app_settings WHERE id = 'GLOBAL_SETTINGS'")
    suspend fun hasCompletedOnboarding(): Boolean?
}
