package com.mespapiers.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mespapiers.app.data.repository.ProfileRepository
import com.mespapiers.app.data.repository.SettingsRepository
import com.mespapiers.app.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _hasCompletedOnboarding = MutableStateFlow<Boolean?>(null)
    val hasCompletedOnboarding: StateFlow<Boolean?> = _hasCompletedOnboarding.asStateFlow()

    private val _hasProfile = MutableStateFlow<Boolean?>(null)
    val hasProfile: StateFlow<Boolean?> = _hasProfile.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _profileName = MutableStateFlow("Moi")
    val profileName: StateFlow<String> = _profileName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            settingsRepository.initializeSettings()
            settingsRepository.initializeStats()

            val completed = settingsRepository.hasCompletedOnboarding()
            _hasCompletedOnboarding.value = completed

            if (completed) {
                val count = profileRepository.getProfileCount()
                _hasProfile.value = count > 0

                if (count > 0) {
                    // Increment app open count
                    settingsRepository.incrementOpenCount()
                }
            } else {
                _hasProfile.value = false
            }
        }
    }

    fun nextPage() {
        _currentPage.value = (_currentPage.value + 1).coerceAtMost(5)
    }

    fun previousPage() {
        _currentPage.value = (_currentPage.value - 1).coerceAtLeast(0)
    }

    fun updateProfileName(name: String) {
        _profileName.value = name
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            // Create profile
            val profile = profileRepository.createProfile(_profileName.value.ifBlank { "Moi" })

            // Set as current profile
            settingsRepository.setCurrentProfileId(profile.id)

            // Mark onboarding as complete
            settingsRepository.setOnboardingCompleted(true)

            // Increment first open count
            settingsRepository.incrementOpenCount()

            _isLoading.value = false
            onComplete()
        }
    }
}
