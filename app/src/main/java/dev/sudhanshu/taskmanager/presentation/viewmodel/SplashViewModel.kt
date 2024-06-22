package dev.sudhanshu.taskmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        checkUserSignInStatus()
    }

    private fun checkUserSignInStatus() {
        viewModelScope.launch {
            delay(2000) // Simulate a splash screen delay
            val currentUser = auth.currentUser
            if (currentUser != null) {
                _uiState.value = UiState.SignedIn
            } else {
                _uiState.value = UiState.SignedOut
            }
        }
    }

    sealed class UiState {
        data object Loading : UiState()
        data object SignedIn : UiState()
        data object SignedOut : UiState()
    }
}

