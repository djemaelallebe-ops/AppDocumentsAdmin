package com.mespapiers.app.domain.model

import java.time.Instant
import java.time.LocalDate

enum class DocumentType(val displayName: String, val categoryType: CategoryType) {
    // Identity
    IDENTITY_CARD("Carte d'identite", CategoryType.IDENTITY),
    PASSPORT("Passeport", CategoryType.IDENTITY),
    DRIVING_LICENSE("Permis de conduire", CategoryType.IDENTITY),

    // Health & Family
    HEALTH_CARD("Carte Vitale", CategoryType.FAMILY_HEALTH),
    MUTUAL_CARD("Carte Mutuelle", CategoryType.FAMILY_HEALTH),
    FAMILY_BOOK("Livret de famille", CategoryType.FAMILY_HEALTH),

    // Housing & Bills
    ELECTRICITY_BILL("Facture Electricite", CategoryType.HOUSING_BILLS),
    GAS_BILL("Facture Gaz", CategoryType.HOUSING_BILLS),
    WATER_BILL("Facture Eau", CategoryType.HOUSING_BILLS),
    INTERNET_BILL("Facture Internet", CategoryType.HOUSING_BILLS),
    RENT_RECEIPT("Quittance de Loyer", CategoryType.HOUSING_BILLS),
    HOME_INSURANCE("Assurance Habitation", CategoryType.HOUSING_BILLS),

    // Income & Tax
    PAYSLIP("Bulletin de Salaire", CategoryType.INCOME_TAX),
    TAX_NOTICE("Avis d'Impot", CategoryType.INCOME_TAX),

    // Other
    OTHER("Autre document", CategoryType.OTHER)
}

enum class DocumentState {
    ACTIVE,
    EXPIRED,
    EXPIRING_SOON // Within 90 days
}

data class Document(
    val id: String,
    val profileId: String,
    val categoryId: String,
    val title: String,
    val documentType: DocumentType,
    val periodMonth: Int? = null,
    val periodYear: Int? = null,
    val expiryDate: LocalDate? = null,
    val isFavourite: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
    val latestVersion: DocumentVersion? = null,
    val versionCount: Int = 0
) {
    val state: DocumentState
        get() {
            if (expiryDate == null) return DocumentState.ACTIVE
            val today = LocalDate.now()
            return when {
                expiryDate.isBefore(today) -> DocumentState.EXPIRED
                expiryDate.isBefore(today.plusDays(90)) -> DocumentState.EXPIRING_SOON
                else -> DocumentState.ACTIVE
            }
        }

    val exportFileName: String
        get() = buildExportFileName()

    private fun buildExportFileName(): String {
        val baseName = title.trim()
        val suffix = when (documentType) {
            DocumentType.ELECTRICITY_BILL,
            DocumentType.GAS_BILL,
            DocumentType.WATER_BILL,
            DocumentType.INTERNET_BILL,
            DocumentType.RENT_RECEIPT,
            DocumentType.PAYSLIP -> {
                if (periodMonth != null && periodYear != null) {
                    " ${getFrenchMonth(periodMonth)} $periodYear"
                } else ""
            }
            DocumentType.TAX_NOTICE -> {
                if (periodYear != null) " $periodYear" else ""
            }
            else -> ""
        }

        val fileName = "$baseName$suffix"
            .replace(Regex("[/\\\\:*?\"<>|]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        return "$fileName.pdf"
    }

    companion object {
        private fun getFrenchMonth(month: Int): String = when (month) {
            1 -> "Janvier"
            2 -> "Fevrier"
            3 -> "Mars"
            4 -> "Avril"
            5 -> "Mai"
            6 -> "Juin"
            7 -> "Juillet"
            8 -> "Aout"
            9 -> "Septembre"
            10 -> "Octobre"
            11 -> "Novembre"
            12 -> "Decembre"
            else -> ""
        }
    }
}
