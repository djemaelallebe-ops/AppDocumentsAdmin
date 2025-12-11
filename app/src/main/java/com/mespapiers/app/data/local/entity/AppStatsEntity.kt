package com.mespapiers.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_stats")
data class AppStatsEntity(
    @PrimaryKey
    val id: String = "GLOBAL_STATS",

    @ColumnInfo(name = "app_open_count")
    val appOpenCount: Int = 0,

    @ColumnInfo(name = "last_support_prompt_at")
    val lastSupportPromptAt: Long? = null,

    @ColumnInfo(name = "last_rating_prompt_at")
    val lastRatingPromptAt: Long? = null,

    @ColumnInfo(name = "has_rated")
    val hasRated: Boolean = false,

    @ColumnInfo(name = "rating_prompt_disabled")
    val ratingPromptDisabled: Boolean = false,

    @ColumnInfo(name = "support_prompt_disabled")
    val supportPromptDisabled: Boolean = false
)
