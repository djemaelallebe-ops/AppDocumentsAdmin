package com.mespapiers.app.ui.screens.document

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mespapiers.app.data.repository.CategoryRepository
import com.mespapiers.app.data.repository.DocumentRepository
import com.mespapiers.app.data.repository.SettingsRepository
import com.mespapiers.app.domain.model.DocumentSource
import com.mespapiers.app.domain.model.DocumentType
import com.mespapiers.app.util.FileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentRepository: DocumentRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository,
    private val fileManager: FileManager
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun processScanResult(
        categoryId: String,
        pdfUri: Uri,
        pageCount: Int,
        onComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isProcessing.value = true
            _error.value = null

            try {
                val profileId = settingsRepository.getCurrentProfileId()
                    ?: throw IllegalStateException("No profile selected")

                val category = categoryRepository.getCategoryById(categoryId)
                    ?: throw IllegalStateException("Category not found")

                // Generate IDs
                val documentId = UUID.randomUUID().toString()
                val versionId = UUID.randomUUID().toString()

                // Copy scanned PDF to app storage
                val filePath = withContext(Dispatchers.IO) {
                    fileManager.copyFileFromUri(pdfUri, profileId, documentId, versionId)
                } ?: throw IllegalStateException("Failed to save scanned document")

                // Get default title based on category
                val defaultTitle = getDefaultTitle(category.type)
                val documentType = getDefaultDocumentType(category.type)

                // Create document
                val document = documentRepository.createDocument(
                    profileId = profileId,
                    categoryId = categoryId,
                    title = defaultTitle,
                    documentType = documentType,
                    filePath = filePath,
                    pagesCount = pageCount,
                    source = DocumentSource.SCANNED
                )

                _isProcessing.value = false
                onComplete(document.id)

            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors du traitement"
                _isProcessing.value = false
            }
        }
    }

    private fun getDefaultTitle(categoryType: com.mespapiers.app.domain.model.CategoryType): String {
        return when (categoryType) {
            com.mespapiers.app.domain.model.CategoryType.IDENTITY -> "Piece d'identite"
            com.mespapiers.app.domain.model.CategoryType.FAMILY_HEALTH -> "Document sante"
            com.mespapiers.app.domain.model.CategoryType.HOUSING_BILLS -> "Facture"
            com.mespapiers.app.domain.model.CategoryType.INCOME_TAX -> "Document revenus"
            com.mespapiers.app.domain.model.CategoryType.OTHER -> "Document"
        }
    }

    private fun getDefaultDocumentType(categoryType: com.mespapiers.app.domain.model.CategoryType): DocumentType {
        return when (categoryType) {
            com.mespapiers.app.domain.model.CategoryType.IDENTITY -> DocumentType.IDENTITY_CARD
            com.mespapiers.app.domain.model.CategoryType.FAMILY_HEALTH -> DocumentType.HEALTH_CARD
            com.mespapiers.app.domain.model.CategoryType.HOUSING_BILLS -> DocumentType.ELECTRICITY_BILL
            com.mespapiers.app.domain.model.CategoryType.INCOME_TAX -> DocumentType.PAYSLIP
            com.mespapiers.app.domain.model.CategoryType.OTHER -> DocumentType.OTHER
        }
    }
}
