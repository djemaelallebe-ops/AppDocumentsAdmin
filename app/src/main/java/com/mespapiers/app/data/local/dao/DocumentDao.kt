package com.mespapiers.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mespapiers.app.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents WHERE profile_id = :profileId ORDER BY updated_at DESC")
    fun getDocumentsByProfile(profileId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE category_id = :categoryId ORDER BY updated_at DESC")
    fun getDocumentsByCategory(categoryId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE category_id = :categoryId ORDER BY updated_at DESC")
    suspend fun getDocumentsByCategorySync(categoryId: String): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE id = :id")
    fun getDocumentByIdFlow(id: String): Flow<DocumentEntity?>

    @Query("SELECT * FROM documents WHERE profile_id = :profileId AND document_type = :type ORDER BY updated_at DESC")
    suspend fun getDocumentsByType(profileId: String, type: String): List<DocumentEntity>

    @Query("""
        SELECT * FROM documents
        WHERE profile_id = :profileId
        AND expiry_date IS NOT NULL
        AND expiry_date < :timestamp
        ORDER BY expiry_date ASC
    """)
    suspend fun getExpiredDocuments(profileId: String, timestamp: Long): List<DocumentEntity>

    @Query("""
        SELECT * FROM documents
        WHERE profile_id = :profileId
        AND expiry_date IS NOT NULL
        AND expiry_date BETWEEN :startTimestamp AND :endTimestamp
        ORDER BY expiry_date ASC
    """)
    suspend fun getDocumentsExpiringBetween(
        profileId: String,
        startTimestamp: Long,
        endTimestamp: Long
    ): List<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: String)

    @Query("DELETE FROM documents WHERE profile_id = :profileId")
    suspend fun deleteDocumentsByProfile(profileId: String)

    @Query("DELETE FROM documents WHERE category_id = :categoryId")
    suspend fun deleteDocumentsByCategory(categoryId: String)

    @Query("SELECT COUNT(*) FROM documents WHERE category_id = :categoryId")
    suspend fun getDocumentCountByCategory(categoryId: String): Int

    @Query("SELECT COUNT(*) FROM documents WHERE profile_id = :profileId")
    suspend fun getDocumentCountByProfile(profileId: String): Int

    @Query("UPDATE documents SET is_favourite = :isFavourite, updated_at = :timestamp WHERE id = :id")
    suspend fun setFavourite(id: String, isFavourite: Boolean, timestamp: Long)
}
