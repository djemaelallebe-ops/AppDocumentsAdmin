package com.mespapiers.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mespapiers.app.data.repository.ProfileRepository
import com.mespapiers.app.data.repository.SettingsRepository
import com.mespapiers.app.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val profiles = profileRepository.getAllProfiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentProfileId = MutableStateFlow<String?>(null)
    val currentProfileId: StateFlow<String?> = _currentProfileId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _profileName = MutableStateFlow("")
    val profileName: StateFlow<String> = _profileName.asStateFlow()

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            _currentProfileId.value = settingsRepository.getCurrentProfileId()
        }
    }

    fun selectProfile(profile: Profile, onSelected: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.setCurrentProfileId(profile.id)
            _currentProfileId.value = profile.id
            settingsRepository.incrementOpenCount()
            onSelected()
        }
    }

    fun updateProfileName(name: String) {
        _profileName.value = name
    }

    fun createProfile(onCreated: () -> Unit) {
        val name = _profileName.value.trim()
        if (name.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            val profile = profileRepository.createProfile(name)
            settingsRepository.setCurrentProfileId(profile.id)
            _currentProfileId.value = profile.id
            _profileName.value = ""
            _isLoading.value = false
            onCreated()
        }
    }

    fun renameProfile(profileId: String, newName: String) {
        viewModelScope.launch {
            profileRepository.renameProfile(profileId, newName)
        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profile.id)

            // If deleting current profile, switch to another one
            if (_currentProfileId.value == profile.id) {
                val remaining = profileRepository.getProfileCount()
                if (remaining > 0) {
                    // Will be handled by UI to select new profile
                    _currentProfileId.value = null
                }
            }
        }
    }
}
