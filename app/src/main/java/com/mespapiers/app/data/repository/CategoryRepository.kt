package com.mespapiers.app.data.repository

import com.mespapiers.app.data.local.dao.CategoryDao
import com.mespapiers.app.data.local.dao.DocumentDao
import com.mespapiers.app.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val documentDao: DocumentDao
) {
    fun getCategoriesByProfile(profileId: String): Flow<List<Category>> {
        return categoryDao.getCategoriesByProfile(profileId).map { categories ->
            categories.map { entity ->
                val count = documentDao.getDocumentCountByCategory(entity.id)
                entity.toDomain(documentCount = count)
            }
        }
    }

    suspend fun getCategoriesByProfileSync(profileId: String): List<Category> {
        return categoryDao.getCategoriesByProfileSync(profileId).map { entity ->
            val count = documentDao.getDocumentCountByCategory(entity.id)
            entity.toDomain(documentCount = count)
        }
    }

    suspend fun getCategoryById(id: String): Category? {
        val entity = categoryDao.getCategoryById(id) ?: return null
        val count = documentDao.getDocumentCountByCategory(id)
        return entity.toDomain(documentCount = count)
    }

    suspend fun updateCategory(category: Category) {
        val updated = category.copy(updatedAt = Instant.now())
        categoryDao.updateCategory(updated.toEntity())
    }

    suspend fun renameCategory(id: String, customLabel: String?) {
        val category = categoryDao.getCategoryById(id) ?: return
        val updated = category.copy(
            customLabel = customLabel?.trim()?.ifBlank { null },
            updatedAt = System.currentTimeMillis()
        )
        categoryDao.updateCategory(updated)
    }
}
