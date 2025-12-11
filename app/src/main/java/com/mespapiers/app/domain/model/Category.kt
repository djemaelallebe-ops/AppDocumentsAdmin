package com.mespapiers.app.domain.model

import java.time.Instant

enum class CategoryType(val displayName: String) {
    IDENTITY("Identite & Permis"),
    FAMILY_HEALTH("Sante & Famille"),
    HOUSING_BILLS("Logement & Factures"),
    INCOME_TAX("Revenus & Impots"),
    OTHER("Autres documents")
}

data class Category(
    val id: String,
    val profileId: String,
    val type: CategoryType,
    val customLabel: String? = null,
    val orderIndex: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val documentCount: Int = 0
) {
    val displayName: String
        get() = customLabel ?: type.displayName
}
