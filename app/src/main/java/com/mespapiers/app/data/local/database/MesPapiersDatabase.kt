package com.mespapiers.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mespapiers.app.data.local.dao.AppSettingsDao
import com.mespapiers.app.data.local.dao.AppStatsDao
import com.mespapiers.app.data.local.dao.CategoryDao
import com.mespapiers.app.data.local.dao.DocumentDao
import com.mespapiers.app.data.local.dao.DocumentVersionDao
import com.mespapiers.app.data.local.dao.ProfileDao
import com.mespapiers.app.data.local.entity.AppSettingsEntity
import com.mespapiers.app.data.local.entity.AppStatsEntity
import com.mespapiers.app.data.local.entity.CategoryEntity
import com.mespapiers.app.data.local.entity.DocumentEntity
import com.mespapiers.app.data.local.entity.DocumentVersionEntity
import com.mespapiers.app.data.local.entity.ProfileEntity

@Database(
    entities = [
        ProfileEntity::class,
        CategoryEntity::class,
        DocumentEntity::class,
        DocumentVersionEntity::class,
        AppSettingsEntity::class,
        AppStatsEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MesPapiersDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun categoryDao(): CategoryDao
    abstract fun documentDao(): DocumentDao
    abstract fun documentVersionDao(): DocumentVersionDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun appStatsDao(): AppStatsDao

    companion object {
        const val DATABASE_NAME = "mes_papiers_db"
    }
}
