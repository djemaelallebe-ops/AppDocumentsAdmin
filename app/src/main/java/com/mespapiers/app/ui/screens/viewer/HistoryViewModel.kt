package com.mespapiers.app.ui.screens.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mespapiers.app.data.repository.DocumentRepository
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.domain.model.DocumentVersion
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
class HistoryViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val fileManager: FileManager
) : ViewModel() {

    private val _document = MutableStateFlow<Document?>(null)
    val document: StateFlow<Document?> = _document.asStateFlow()

    private val _versions = MutableStateFlow<List<DocumentVersion>>(emptyList())
    val versions: StateFlow<List<DocumentVersion>> = _versions.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentDocumentId: String? = null

    fun loadDocument(documentId: String) {
        currentDocumentId = documentId

        viewModelScope.launch {
            _isLoading.value = true

            val doc = documentRepository.getDocumentByIdSync(documentId)
            _document.value = doc

            val versionList = documentRepository.getVersionsByDocumentSync(documentId)
            _versions.value = versionList

            _isLoading.value = false
        }
    }

    fun deleteVersion(versionId: String) {
        viewModelScope.launch {
            try {
                val version = documentRepository.getVersionById(versionId) ?: return@launch

                // Delete file
                withContext(Dispatchers.IO) {
                    fileManager.deleteFile(version.filePath)
                }

                // Soft delete in database
                documentRepository.deleteVersion(versionId)

                // Reload versions
                currentDocumentId?.let { loadDocument(it) }

            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
