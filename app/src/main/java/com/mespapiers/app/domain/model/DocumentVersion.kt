package com.mespapiers.app.domain.model

import java.time.Instant

enum class DocumentSource {
    SCANNED,
    IMPORTED
}

data class DocumentVersion(
    val id: String,
    val documentId: String,
    val filePath: String,
    val pagesCount: Int,
    val source: DocumentSource,
    val createdAt: Instant,
    val isDeleted: Boolean = false
)
