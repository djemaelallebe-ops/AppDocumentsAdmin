package com.mespapiers.app.ui.screens.document

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mespapiers.app.data.repository.DocumentRepository
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.domain.model.DocumentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class DocumentInfoViewModel @Inject constructor(
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _document = MutableStateFlow<Document?>(null)
    val document: StateFlow<Document?> = _document.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _documentType = MutableStateFlow<DocumentType?>(null)
    val documentType: StateFlow<DocumentType?> = _documentType.asStateFlow()

    private val _periodMonth = MutableStateFlow<Int?>(null)
    val periodMonth: StateFlow<Int?> = _periodMonth.asStateFlow()

    private val _periodYear = MutableStateFlow<Int?>(null)
    val periodYear: StateFlow<Int?> = _periodYear.asStateFlow()

    private val _expiryDate = MutableStateFlow<String?>(null)
    val expiryDate: StateFlow<String?> = _expiryDate.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun loadDocument(documentId: String) {
        viewModelScope.launch {
            val doc = documentRepository.getDocumentByIdSync(documentId) ?: return@launch
            _document.value = doc
            _title.value = doc.title
            _documentType.value = doc.documentType
            _periodMonth.value = doc.periodMonth
            _periodYear.value = doc.periodYear
            _expiryDate.value = doc.expiryDate?.format(dateFormatter)
        }
    }

    fun updateTitle(title: String) {
        _title.value = title
    }

    fun updateDocumentType(type: DocumentType) {
        _documentType.value = type
    }

    fun updatePeriodMonth(month: Int?) {
        _periodMonth.value = month
    }

    fun updatePeriodYear(year: Int?) {
        _periodYear.value = year
    }

    fun updateExpiryDate(date: String) {
        _expiryDate.value = date
    }

    fun saveDocument(onSaved: () -> Unit) {
        val currentDoc = _document.value ?: return

        viewModelScope.launch {
            _isSaving.value = true

            val parsedExpiryDate = try {
                _expiryDate.value?.let { LocalDate.parse(it, dateFormatter) }
            } catch (e: DateTimeParseException) {
                null
            }

            val updatedDoc = currentDoc.copy(
                title = _title.value.trim(),
                documentType = _documentType.value ?: currentDoc.documentType,
                periodMonth = _periodMonth.value,
                periodYear = _periodYear.value,
                expiryDate = parsedExpiryDate
            )

            documentRepository.updateDocument(updatedDoc)

            _isSaving.value = false
            onSaved()
        }
    }
}
