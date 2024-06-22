package app.inscribe.presentation.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sudhanshu.taskmanager.domain.model.User
import dev.sudhanshu.taskmanager.domain.usecase.GetUserUseCase
import dev.sudhanshu.taskmanager.domain.usecase.SaveUserUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState


    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
                val user = auth.currentUser
                user?.let {
                    saveUserUseCase.invoke(User(it.uid, it.displayName ?: "", it.email ?: "", it.photoUrl?.toString()))
                }
                _uiState.value = UiState.LoggedIn(user)
            } catch (e: Exception) {
                _uiState.value = UiState.LoggedOut
            }
        }
    }

    sealed class UiState {
        data object Loading : UiState()
        data object LoggedOut : UiState()
        data class LoggedIn(val user: FirebaseUser?) : UiState()
    }
}
