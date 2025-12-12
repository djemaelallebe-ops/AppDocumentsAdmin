package com.mespapiers.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mespapiers.app.data.local.entity.AppStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppStatsDao {

    @Query("SELECT * FROM app_stats WHERE id = 'GLOBAL_STATS'")
    fun getStats(): Flow<AppStatsEntity?>

    @Query("SELECT * FROM app_stats WHERE id = 'GLOBAL_STATS'")
    suspend fun getStatsSync(): AppStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: AppStatsEntity)

    @Update
    suspend fun updateStats(stats: AppStatsEntity)

    @Query("UPDATE app_stats SET app_open_count = app_open_count + 1 WHERE id = 'GLOBAL_STATS'")
    suspend fun incrementOpenCount()

    @Query("UPDATE app_stats SET last_support_prompt_at = :timestamp WHERE id = 'GLOBAL_STATS'")
    suspend fun setLastSupportPromptAt(timestamp: Long)

    @Query("UPDATE app_stats SET last_rating_prompt_at = :timestamp WHERE id = 'GLOBAL_STATS'")
    suspend fun setLastRatingPromptAt(timestamp: Long)

    @Query("UPDATE app_stats SET has_rated = :hasRated WHERE id = 'GLOBAL_STATS'")
    suspend fun setHasRated(hasRated: Boolean)

    @Query("UPDATE app_stats SET rating_prompt_disabled = :disabled WHERE id = 'GLOBAL_STATS'")
    suspend fun setRatingPromptDisabled(disabled: Boolean)

    @Query("UPDATE app_stats SET support_prompt_disabled = :disabled WHERE id = 'GLOBAL_STATS'")
    suspend fun setSupportPromptDisabled(disabled: Boolean)

    @Query("SELECT app_open_count FROM app_stats WHERE id = 'GLOBAL_STATS'")
    suspend fun getOpenCount(): Int?
}
