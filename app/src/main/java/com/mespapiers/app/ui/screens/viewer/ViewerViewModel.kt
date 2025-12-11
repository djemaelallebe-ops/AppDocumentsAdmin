package com.mespapiers.app.ui.screens.viewer

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mespapiers.app.data.repository.DocumentRepository
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.util.FileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewerViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val fileManager: FileManager
) : ViewModel() {

    private val _document = MutableStateFlow<Document?>(null)
    val document: StateFlow<Document?> = _document.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadDocument(documentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val doc = documentRepository.getDocumentByIdSync(documentId)
                if (doc != null) {
                    _document.value = doc

                    // Check if file exists
                    doc.latestVersion?.let { version ->
                        if (!fileManager.fileExists(version.filePath)) {
                            _error.value = "Fichier introuvable"
                        }
                    } ?: run {
                        _error.value = "Aucune version disponible"
                    }
                } else {
                    _error.value = "Document introuvable"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erreur de chargement"
            }

            _isLoading.value = false
        }
    }

    fun shareDocument(context: Context, onIntent: (Intent) -> Unit) {
        val doc = _document.value ?: return
        val version = doc.latestVersion ?: return

        viewModelScope.launch {
            try {
                val exportFile = withContext(Dispatchers.IO) {
                    fileManager.createTempExportFile(version.filePath, doc.exportFileName)
                }

                exportFile?.let { file ->
                    val intent = fileManager.createShareIntent(file)
                    onIntent(intent)
                }
            } catch (e: Exception) {
                _error.value = "Erreur lors du partage"
            }
        }
    }

    fun deleteDocument(onDeleted: () -> Unit) {
        val doc = _document.value ?: return

        viewModelScope.launch {
            try {
                // Delete files
                withContext(Dispatchers.IO) {
                    fileManager.deleteDocumentDir(doc.profileId, doc.id)
                }

                // Delete from database
                documentRepository.deleteDocument(doc.id)

                onDeleted()
            } catch (e: Exception) {
                _error.value = "Erreur lors de la suppression"
            }
        }
    }
}
