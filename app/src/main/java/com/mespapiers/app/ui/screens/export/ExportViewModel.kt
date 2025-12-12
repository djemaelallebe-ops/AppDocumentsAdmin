package com.mespapiers.app.ui.screens.export

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mespapiers.app.data.repository.CategoryRepository
import com.mespapiers.app.data.repository.DocumentRepository
import com.mespapiers.app.data.repository.SettingsRepository
import com.mespapiers.app.domain.model.Category
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.util.FileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class CategoryWithDocuments(
    val category: Category,
    val documents: List<Document>
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val documentRepository: DocumentRepository,
    private val settingsRepository: SettingsRepository,
    private val fileManager: FileManager
) : ViewModel() {

    private val _categoriesWithDocuments = MutableStateFlow<List<CategoryWithDocuments>>(emptyList())
    val categoriesWithDocuments: StateFlow<List<CategoryWithDocuments>> = _categoriesWithDocuments.asStateFlow()

    private val _selectedDocuments = MutableStateFlow<Set<String>>(emptySet())
    val selectedDocuments: StateFlow<Set<String>> = _selectedDocuments.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportSuccess = MutableStateFlow(false)
    val exportSuccess: StateFlow<Boolean> = _exportSuccess.asStateFlow()

    private var currentProfileId: String? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val profileId = settingsRepository.getCurrentProfileId() ?: return@launch
            currentProfileId = profileId

            val categories = categoryRepository.getCategoriesByProfileSync(profileId)
            val categoriesWithDocs = categories.map { category ->
                val documents = documentRepository.getDocumentsByCategorySync(category.id)
                CategoryWithDocuments(category, documents)
            }
            _categoriesWithDocuments.value = categoriesWithDocs
        }
    }

    fun toggleDocument(documentId: String) {
        val current = _selectedDocuments.value.toMutableSet()
        if (current.contains(documentId)) {
            current.remove(documentId)
        } else {
            current.add(documentId)
        }
        _selectedDocuments.value = current
    }

    fun selectAll() {
        val allDocIds = _categoriesWithDocuments.value
            .flatMap { it.documents }
            .map { it.id }
            .toSet()
        _selectedDocuments.value = allDocIds
    }

    fun deselectAll() {
        _selectedDocuments.value = emptySet()
    }

    fun generateZip(onIntent: (Intent) -> Unit) {
        val profileId = currentProfileId ?: return
        val selectedIds = _selectedDocuments.value

        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            _isExporting.value = true

            try {
                val allDocuments = _categoriesWithDocuments.value.flatMap { it.documents }
                val categories = _categoriesWithDocuments.value.map { it.category }

                val zipEntries = mutableListOf<FileManager.ZipFileEntry>()

                selectedIds.forEach { docId ->
                    val document = allDocuments.find { it.id == docId } ?: return@forEach
                    val version = document.latestVersion ?: return@forEach
                    val category = categories.find { it.id == document.categoryId } ?: return@forEach

                    val folderName = category.displayName
                    val fileName = document.exportFileName
                    val zipPath = "$folderName/$fileName"

                    zipEntries.add(
                        FileManager.ZipFileEntry(
                            sourcePath = version.filePath,
                            zipPath = zipPath
                        )
                    )
                }

                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                val zipFileName = "MesPapiers_${dateFormat.format(Date())}.zip"

                val zipFile = withContext(Dispatchers.IO) {
                    fileManager.createZipExport(profileId, zipEntries, zipFileName)
                }

                zipFile?.let { file ->
                    val intent = fileManager.createZipShareIntent(file)
                    _exportSuccess.value = true
                    onIntent(intent)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            _isExporting.value = false
        }
    }
}
