package com.mespapiers.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profile_id"]),
        Index(value = ["category_id"]),
        Index(value = ["document_type"]),
        Index(value = ["period_year", "period_month"])
    ]
)
data class DocumentEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "document_type")
    val documentType: String,

    @ColumnInfo(name = "period_month")
    val periodMonth: Int? = null,

    @ColumnInfo(name = "period_year")
    val periodYear: Int? = null,

    @ColumnInfo(name = "expiry_date")
    val expiryDate: Long? = null,

    @ColumnInfo(name = "is_favourite")
    val isFavourite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
