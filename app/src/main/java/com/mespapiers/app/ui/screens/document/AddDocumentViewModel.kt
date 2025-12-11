package com.mespapiers.app.ui.screens.document

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
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
class AddDocumentViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentRepository: DocumentRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository,
    private val fileManager: FileManager
) : ViewModel() {

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun importPdf(categoryId: String, uri: Uri, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _isImporting.value = true
            _error.value = null

            try {
                val profileId = settingsRepository.getCurrentProfileId()
                    ?: throw IllegalStateException("No profile selected")

                val category = categoryRepository.getCategoryById(categoryId)
                    ?: throw IllegalStateException("Category not found")

                // Get page count from PDF
                val pageCount = withContext(Dispatchers.IO) {
                    getPdfPageCount(uri)
                }

                // Generate IDs
                val documentId = UUID.randomUUID().toString()
                val versionId = UUID.randomUUID().toString()

                // Copy file to app storage
                val filePath = withContext(Dispatchers.IO) {
                    fileManager.copyFileFromUri(uri, profileId, documentId, versionId)
                } ?: throw IllegalStateException("Failed to copy file")

                // Get a default title based on category type
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
                    source = DocumentSource.IMPORTED
                )

                _isImporting.value = false
                onComplete(document.id)

            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur lors de l'import"
                _isImporting.value = false
            }
        }
    }

    private fun getPdfPageCount(uri: Uri): Int {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    renderer.pageCount
                }
            } ?: 1
        } catch (e: Exception) {
            1
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
