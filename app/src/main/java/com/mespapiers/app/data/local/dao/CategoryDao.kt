package com.mespapiers.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mespapiers.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE profile_id = :profileId ORDER BY order_index ASC")
    fun getCategoriesByProfile(profileId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE profile_id = :profileId ORDER BY order_index ASC")
    suspend fun getCategoriesByProfileSync(profileId: String): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: String)

    @Query("DELETE FROM categories WHERE profile_id = :profileId")
    suspend fun deleteCategoriesByProfile(profileId: String)
}
