package com.mespapiers.app.domain.model

import java.time.Instant

data class AppStats(
    val appOpenCount: Int = 0,
    val lastSupportPromptAt: Instant? = null,
    val lastRatingPromptAt: Instant? = null,
    val hasRated: Boolean = false,
    val ratingPromptDisabled: Boolean = false,
    val supportPromptDisabled: Boolean = false
) {
    fun shouldShowSupportPrompt(): Boolean {
        if (supportPromptDisabled) return false
        if (lastSupportPromptAt == null) return true
        val thirtyDaysAgo = Instant.now().minusSeconds(30 * 24 * 60 * 60)
        return lastSupportPromptAt.isBefore(thirtyDaysAgo)
    }

    fun shouldShowRatingPrompt(): Boolean {
        if (hasRated) return false
        if (ratingPromptDisabled) return false
        return appOpenCount > 0 && appOpenCount % 10 == 0
    }
}
