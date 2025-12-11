package com.mespapiers.app.data.repository

import com.mespapiers.app.data.local.dao.DocumentDao
import com.mespapiers.app.data.local.dao.DocumentVersionDao
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.domain.model.DocumentSource
import com.mespapiers.app.domain.model.DocumentType
import com.mespapiers.app.domain.model.DocumentVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao,
    private val documentVersionDao: DocumentVersionDao
) {
    fun getDocumentsByCategory(categoryId: String): Flow<List<Document>> {
        return documentDao.getDocumentsByCategory(categoryId).map { documents ->
            documents.map { entity ->
                val latestVersion = documentVersionDao.getLatestVersion(entity.id)?.toDomain()
                val versionCount = documentVersionDao.getVersionCount(entity.id)
                entity.toDomain(latestVersion = latestVersion, versionCount = versionCount)
            }
        }
    }

    suspend fun getDocumentsByCategorySync(categoryId: String): List<Document> {
        return documentDao.getDocumentsByCategorySync(categoryId).map { entity ->
            val latestVersion = documentVersionDao.getLatestVersion(entity.id)?.toDomain()
            val versionCount = documentVersionDao.getVersionCount(entity.id)
            entity.toDomain(latestVersion = latestVersion, versionCount = versionCount)
        }
    }

    fun getDocumentById(id: String): Flow<Document?> {
        return combine(
            documentDao.getDocumentByIdFlow(id),
            documentVersionDao.getLatestVersionFlow(id)
        ) { document, version ->
            document?.let { entity ->
                val versionCount = documentVersionDao.getVersionCount(entity.id)
                entity.toDomain(
                    latestVersion = version?.toDomain(),
                    versionCount = versionCount
                )
            }
        }
    }

    suspend fun getDocumentByIdSync(id: String): Document? {
        val entity = documentDao.getDocumentById(id) ?: return null
        val latestVersion = documentVersionDao.getLatestVersion(id)?.toDomain()
        val versionCount = documentVersionDao.getVersionCount(id)
        return entity.toDomain(latestVersion = latestVersion, versionCount = versionCount)
    }

    suspend fun createDocument(
        profileId: String,
        categoryId: String,
        title: String,
        documentType: DocumentType,
        periodMonth: Int? = null,
        periodYear: Int? = null,
        expiryDate: LocalDate? = null,
        filePath: String,
        pagesCount: Int,
        source: DocumentSource
    ): Document {
        val now = Instant.now()
        val documentId = UUID.randomUUID().toString()

        val document = Document(
            id = documentId,
            profileId = profileId,
            categoryId = categoryId,
            title = title.trim(),
            documentType = documentType,
            periodMonth = periodMonth,
            periodYear = periodYear,
            expiryDate = expiryDate,
            createdAt = now,
            updatedAt = now
        )
        documentDao.insertDocument(document.toEntity())

        val version = DocumentVersion(
            id = UUID.randomUUID().toString(),
            documentId = documentId,
            filePath = filePath,
            pagesCount = pagesCount,
            source = source,
            createdAt = now
        )
        documentVersionDao.insertVersion(version.toEntity())

        return document.copy(latestVersion = version, versionCount = 1)
    }

    suspend fun addVersion(
        documentId: String,
        filePath: String,
        pagesCount: Int,
        source: DocumentSource
    ): DocumentVersion {
        val now = Instant.now()
        val version = DocumentVersion(
            id = UUID.randomUUID().toString(),
            documentId = documentId,
            filePath = filePath,
            pagesCount = pagesCount,
            source = source,
            createdAt = now
        )
        documentVersionDao.insertVersion(version.toEntity())

        // Update document's updatedAt
        documentDao.getDocumentById(documentId)?.let { entity ->
            documentDao.updateDocument(entity.copy(updatedAt = now.toEpochMilli()))
        }

        return version
    }

    suspend fun updateDocument(document: Document) {
        val updated = document.copy(updatedAt = Instant.now())
        documentDao.updateDocument(updated.toEntity())
    }

    suspend fun deleteDocument(id: String) {
        documentDao.deleteDocumentById(id)
    }

    suspend fun setFavourite(id: String, isFavourite: Boolean) {
        documentDao.setFavourite(id, isFavourite, System.currentTimeMillis())
    }

    fun getVersionsByDocument(documentId: String): Flow<List<DocumentVersion>> {
        return documentVersionDao.getVersionsByDocument(documentId).map { versions ->
            versions.map { it.toDomain() }
        }
    }

    suspend fun getVersionsByDocumentSync(documentId: String): List<DocumentVersion> {
        return documentVersionDao.getVersionsByDocumentSync(documentId).map { it.toDomain() }
    }

    suspend fun getVersionById(id: String): DocumentVersion? {
        return documentVersionDao.getVersionById(id)?.toDomain()
    }

    suspend fun deleteVersion(id: String) {
        documentVersionDao.softDeleteVersion(id)
    }

    suspend fun getExpiredDocuments(profileId: String): List<Document> {
        val now = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return documentDao.getExpiredDocuments(profileId, now).map { entity ->
            val latestVersion = documentVersionDao.getLatestVersion(entity.id)?.toDomain()
            val versionCount = documentVersionDao.getVersionCount(entity.id)
            entity.toDomain(latestVersion = latestVersion, versionCount = versionCount)
        }
    }

    suspend fun getDocumentsExpiringWithin90Days(profileId: String): List<Document> {
        val now = LocalDate.now()
        val start = now.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = now.plusDays(90).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return documentDao.getDocumentsExpiringBetween(profileId, start, end).map { entity ->
            val latestVersion = documentVersionDao.getLatestVersion(entity.id)?.toDomain()
            val versionCount = documentVersionDao.getVersionCount(entity.id)
            entity.toDomain(latestVersion = latestVersion, versionCount = versionCount)
        }
    }
}
