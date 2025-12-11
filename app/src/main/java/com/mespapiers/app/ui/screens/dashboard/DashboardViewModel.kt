package com.mespapiers.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mespapiers.app.data.repository.CategoryRepository
import com.mespapiers.app.data.repository.DocumentRepository
import com.mespapiers.app.data.repository.ProfileRepository
import com.mespapiers.app.data.repository.SettingsRepository
import com.mespapiers.app.domain.model.AppStats
import com.mespapiers.app.domain.model.Category
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryWithDocuments(
    val category: Category,
    val documents: List<Document>
)

data class ReminderState(
    val showSupportReminder: Boolean = false,
    val showRatingReminder: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val categoryRepository: CategoryRepository,
    private val documentRepository: DocumentRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _currentProfileId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentProfile: StateFlow<Profile?> = _currentProfileId
        .flatMapLatest { id ->
            if (id != null) {
                profileRepository.getProfileById(id)
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesWithDocuments: StateFlow<List<CategoryWithDocuments>> = _currentProfileId
        .flatMapLatest { profileId ->
            if (profileId != null) {
                categoryRepository.getCategoriesByProfile(profileId).map { categories ->
                    categories.map { category ->
                        val documents = documentRepository.getDocumentsByCategorySync(category.id)
                        CategoryWithDocuments(category, documents)
                    }
                }
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _reminderState = MutableStateFlow(ReminderState())
    val reminderState: StateFlow<ReminderState> = _reminderState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val profileId = settingsRepository.getCurrentProfileId()
            _currentProfileId.value = profileId
            _isLoading.value = false

            // Check for reminders
            checkReminders()
        }
    }

    private suspend fun checkReminders() {
        val stats = settingsRepository.getStatsSync()
        _reminderState.value = ReminderState(
            showSupportReminder = stats.shouldShowSupportPrompt(),
            showRatingReminder = stats.shouldShowRatingPrompt() && !stats.shouldShowSupportPrompt()
        )
    }

    fun dismissSupportReminder() {
        viewModelScope.launch {
            settingsRepository.recordSupportPromptShown()
            _reminderState.value = _reminderState.value.copy(showSupportReminder = false)
        }
    }

    fun disableSupportReminder() {
        viewModelScope.launch {
            settingsRepository.setSupportPromptDisabled(true)
            _reminderState.value = _reminderState.value.copy(showSupportReminder = false)
        }
    }

    fun dismissRatingReminder() {
        viewModelScope.launch {
            settingsRepository.recordRatingPromptShown()
            _reminderState.value = _reminderState.value.copy(showRatingReminder = false)
        }
    }

    fun markAsRated() {
        viewModelScope.launch {
            settingsRepository.setHasRated(true)
            _reminderState.value = _reminderState.value.copy(showRatingReminder = false)
        }
    }

    fun disableRatingReminder() {
        viewModelScope.launch {
            settingsRepository.setRatingPromptDisabled(true)
            _reminderState.value = _reminderState.value.copy(showRatingReminder = false)
        }
    }

    fun refreshData() {
        loadCurrentProfile()
    }
}
