package com.mespapiers.app.data.repository

import com.mespapiers.app.data.local.dao.CategoryDao
import com.mespapiers.app.data.local.dao.ProfileDao
import com.mespapiers.app.domain.model.Category
import com.mespapiers.app.domain.model.CategoryType
import com.mespapiers.app.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao,
    private val categoryDao: CategoryDao
) {
    fun getAllProfiles(): Flow<List<Profile>> {
        return profileDao.getAllProfiles().map { profiles ->
            profiles.map { it.toDomain() }
        }
    }

    fun getProfileById(id: String): Flow<Profile?> {
        return profileDao.getProfileByIdFlow(id).map { it?.toDomain() }
    }

    suspend fun getProfileByIdSync(id: String): Profile? {
        return profileDao.getProfileById(id)?.toDomain()
    }

    suspend fun createProfile(name: String): Profile {
        val now = Instant.now()
        val profile = Profile(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            createdAt = now,
            updatedAt = now
        )
        profileDao.insertProfile(profile.toEntity())

        // Create default categories for this profile
        createDefaultCategories(profile.id)

        return profile
    }

    private suspend fun createDefaultCategories(profileId: String) {
        val now = Instant.now()
        val categories = CategoryType.entries.mapIndexed { index, type ->
            Category(
                id = UUID.randomUUID().toString(),
                profileId = profileId,
                type = type,
                orderIndex = index,
                createdAt = now,
                updatedAt = now
            ).toEntity()
        }
        categoryDao.insertCategories(categories)
    }

    suspend fun updateProfile(profile: Profile) {
        val updated = profile.copy(updatedAt = Instant.now())
        profileDao.updateProfile(updated.toEntity())
    }

    suspend fun renameProfile(id: String, newName: String) {
        val profile = profileDao.getProfileById(id) ?: return
        val updated = profile.copy(
            name = newName.trim(),
            updatedAt = System.currentTimeMillis()
        )
        profileDao.updateProfile(updated)
    }

    suspend fun deleteProfile(id: String) {
        profileDao.deleteProfileById(id)
    }

    suspend fun archiveProfile(id: String) {
        profileDao.archiveProfile(id, System.currentTimeMillis())
    }

    suspend fun getProfileCount(): Int {
        return profileDao.getProfileCount()
    }
}
