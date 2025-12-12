package com.mespapiers.app.data.repository

import com.mespapiers.app.data.local.entity.AppSettingsEntity
import com.mespapiers.app.data.local.entity.AppStatsEntity
import com.mespapiers.app.data.local.entity.CategoryEntity
import com.mespapiers.app.data.local.entity.DocumentEntity
import com.mespapiers.app.data.local.entity.DocumentVersionEntity
import com.mespapiers.app.data.local.entity.ProfileEntity
import com.mespapiers.app.domain.model.AppSettings
import com.mespapiers.app.domain.model.AppStats
import com.mespapiers.app.domain.model.Category
import com.mespapiers.app.domain.model.CategoryType
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.domain.model.DocumentSource
import com.mespapiers.app.domain.model.DocumentType
import com.mespapiers.app.domain.model.DocumentVersion
import com.mespapiers.app.domain.model.Profile
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// Profile Mappers
fun ProfileEntity.toDomain(): Profile = Profile(
    id = id,
    name = name,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    isArchived = isArchived
)

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    id = id,
    name = name,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    isArchived = isArchived
)

// Category Mappers
fun CategoryEntity.toDomain(documentCount: Int = 0): Category = Category(
    id = id,
    profileId = profileId,
    type = CategoryType.valueOf(type),
    customLabel = customLabel,
    orderIndex = orderIndex,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    documentCount = documentCount
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    profileId = profileId,
    type = type.name,
    customLabel = customLabel,
    orderIndex = orderIndex,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

// Document Mappers
fun DocumentEntity.toDomain(
    latestVersion: DocumentVersion? = null,
    versionCount: Int = 0
): Document = Document(
    id = id,
    profileId = profileId,
    categoryId = categoryId,
    title = title,
    documentType = DocumentType.valueOf(documentType),
    periodMonth = periodMonth,
    periodYear = periodYear,
    expiryDate = expiryDate?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    },
    isFavourite = isFavourite,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    latestVersion = latestVersion,
    versionCount = versionCount
)

fun Document.toEntity(): DocumentEntity = DocumentEntity(
    id = id,
    profileId = profileId,
    categoryId = categoryId,
    title = title,
    documentType = documentType.name,
    periodMonth = periodMonth,
    periodYear = periodYear,
    expiryDate = expiryDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
    isFavourite = isFavourite,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

// DocumentVersion Mappers
fun DocumentVersionEntity.toDomain(): DocumentVersion = DocumentVersion(
    id = id,
    documentId = documentId,
    filePath = filePath,
    pagesCount = pagesCount,
    source = DocumentSource.valueOf(source),
    createdAt = Instant.ofEpochMilli(createdAt),
    isDeleted = isDeleted
)

fun DocumentVersion.toEntity(): DocumentVersionEntity = DocumentVersionEntity(
    id = id,
    documentId = documentId,
    filePath = filePath,
    pagesCount = pagesCount,
    source = source.name,
    createdAt = createdAt.toEpochMilli(),
    isDeleted = isDeleted
)

// AppSettings Mappers
fun AppSettingsEntity.toDomain(): AppSettings = AppSettings(
    biometricEnabled = biometricEnabled,
    tipSupportEnabled = tipSupportEnabled,
    externalDonationVisible = externalDonationVisible,
    hasCompletedOnboarding = hasCompletedOnboarding,
    currentProfileId = currentProfileId
)

fun AppSettings.toEntity(): AppSettingsEntity = AppSettingsEntity(
    biometricEnabled = biometricEnabled,
    tipSupportEnabled = tipSupportEnabled,
    externalDonationVisible = externalDonationVisible,
    hasCompletedOnboarding = hasCompletedOnboarding,
    currentProfileId = currentProfileId
)

// AppStats Mappers
fun AppStatsEntity.toDomain(): AppStats = AppStats(
    appOpenCount = appOpenCount,
    lastSupportPromptAt = lastSupportPromptAt?.let { Instant.ofEpochMilli(it) },
    lastRatingPromptAt = lastRatingPromptAt?.let { Instant.ofEpochMilli(it) },
    hasRated = hasRated,
    ratingPromptDisabled = ratingPromptDisabled,
    supportPromptDisabled = supportPromptDisabled
)

fun AppStats.toEntity(): AppStatsEntity = AppStatsEntity(
    appOpenCount = appOpenCount,
    lastSupportPromptAt = lastSupportPromptAt?.toEpochMilli(),
    lastRatingPromptAt = lastRatingPromptAt?.toEpochMilli(),
    hasRated = hasRated,
    ratingPromptDisabled = ratingPromptDisabled,
    supportPromptDisabled = supportPromptDisabled
)
