package dev.sudhanshu.taskmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sudhanshu.taskmanager.util.Resource
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sudhanshu.taskmanager.domain.usecase.GetUserUseCase
import dev.sudhanshu.taskmanager.domain.usecase.SignOutUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(private val getUserUseCase: GetUserUseCase, private val signOutUserUseCase: SignOutUserUseCase) : ViewModel() {

    private val _user = MutableStateFlow<Resource<dev.sudhanshu.taskmanager.domain.model.User>>(Resource.Loading)
    val user: StateFlow<Resource<dev.sudhanshu.taskmanager.domain.model.User>> = _user

    private val _signOut = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val signOut: StateFlow<Resource<Unit>> = _signOut

    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            val result = getUserUseCase()
            _user.value = result
        }
    }

    fun signOutUser(){
        viewModelScope.launch {
            val result = signOutUserUseCase.invoke()
            _signOut.value = result
        }
    }
}