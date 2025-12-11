package com.mespapiers.app.domain.model

import java.time.Instant

data class Profile(
    val id: String,
    val name: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isArchived: Boolean = false
)
