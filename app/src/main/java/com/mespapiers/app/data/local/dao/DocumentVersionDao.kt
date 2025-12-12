package com.mespapiers.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mespapiers.app.data.local.entity.DocumentVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentVersionDao {

    @Query("""
        SELECT * FROM document_versions
        WHERE document_id = :documentId AND is_deleted = 0
        ORDER BY created_at DESC
    """)
    fun getVersionsByDocument(documentId: String): Flow<List<DocumentVersionEntity>>

    @Query("""
        SELECT * FROM document_versions
        WHERE document_id = :documentId AND is_deleted = 0
        ORDER BY created_at DESC
    """)
    suspend fun getVersionsByDocumentSync(documentId: String): List<DocumentVersionEntity>

    @Query("""
        SELECT * FROM document_versions
        WHERE document_id = :documentId AND is_deleted = 0
        ORDER BY created_at DESC
        LIMIT 1
    """)
    suspend fun getLatestVersion(documentId: String): DocumentVersionEntity?

    @Query("""
        SELECT * FROM document_versions
        WHERE document_id = :documentId AND is_deleted = 0
        ORDER BY created_at DESC
        LIMIT 1
    """)
    fun getLatestVersionFlow(documentId: String): Flow<DocumentVersionEntity?>

    @Query("SELECT * FROM document_versions WHERE id = :id")
    suspend fun getVersionById(id: String): DocumentVersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: DocumentVersionEntity)

    @Update
    suspend fun updateVersion(version: DocumentVersionEntity)

    @Query("UPDATE document_versions SET is_deleted = 1 WHERE id = :id")
    suspend fun softDeleteVersion(id: String)

    @Query("DELETE FROM document_versions WHERE id = :id")
    suspend fun deleteVersionById(id: String)

    @Query("DELETE FROM document_versions WHERE document_id = :documentId")
    suspend fun deleteVersionsByDocument(documentId: String)

    @Query("SELECT COUNT(*) FROM document_versions WHERE document_id = :documentId AND is_deleted = 0")
    suspend fun getVersionCount(documentId: String): Int

    @Query("SELECT * FROM document_versions WHERE is_deleted = 1")
    suspend fun getDeletedVersions(): List<DocumentVersionEntity>

    @Query("DELETE FROM document_versions WHERE is_deleted = 1")
    suspend fun purgeDeletedVersions()
}
