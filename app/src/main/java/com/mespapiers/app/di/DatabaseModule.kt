package com.mespapiers.app.di

import android.content.Context
import androidx.room.Room
import com.mespapiers.app.data.local.dao.AppSettingsDao
import com.mespapiers.app.data.local.dao.AppStatsDao
import com.mespapiers.app.data.local.dao.CategoryDao
import com.mespapiers.app.data.local.dao.DocumentDao
import com.mespapiers.app.data.local.dao.DocumentVersionDao
import com.mespapiers.app.data.local.dao.ProfileDao
import com.mespapiers.app.data.local.database.MesPapiersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MesPapiersDatabase {
        return Room.databaseBuilder(
            context,
            MesPapiersDatabase::class.java,
            MesPapiersDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideProfileDao(database: MesPapiersDatabase): ProfileDao {
        return database.profileDao()
    }

    @Provides
    fun provideCategoryDao(database: MesPapiersDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    fun provideDocumentDao(database: MesPapiersDatabase): DocumentDao {
        return database.documentDao()
    }

    @Provides
    fun provideDocumentVersionDao(database: MesPapiersDatabase): DocumentVersionDao {
        return database.documentVersionDao()
    }

    @Provides
    fun provideAppSettingsDao(database: MesPapiersDatabase): AppSettingsDao {
        return database.appSettingsDao()
    }

    @Provides
    fun provideAppStatsDao(database: MesPapiersDatabase): AppStatsDao {
        return database.appStatsDao()
    }
}
