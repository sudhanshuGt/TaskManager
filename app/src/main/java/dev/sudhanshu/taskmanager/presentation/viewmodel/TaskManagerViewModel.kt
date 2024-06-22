package dev.sudhanshu.taskmanager.presentation.viewmodel

import android.view.View
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.domain.repository.TaskRepository
import dev.sudhanshu.taskmanager.domain.usecase.DeleteTaskUseCase
import dev.sudhanshu.taskmanager.domain.usecase.GetTasksForUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class  TaskManagerViewModel @Inject constructor(
    private val getTasksForUserUseCase: GetTasksForUserUseCase,
    private val deleteTaskForUserUseCase : DeleteTaskUseCase
) : ViewModel() {

    private val _task = MutableStateFlow<List<Task>?>(null)
    val task: StateFlow<List<Task>?> = _task

    fun getTasksForUser(userId: String, onSuccess: (List<Task>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val tasks = getTasksForUserUseCase(userId)
                _task.value = tasks
                onSuccess(tasks)
            } catch (e: Exception) {
                onError(e.message ?: "Failed to get user tasks")
            }
        }
    }

    fun deleteUserTask(taskId: String, onSuccess: (String) -> Unit, onError: (String) -> Unit){
        try {
            viewModelScope.launch {
                deleteTaskForUserUseCase.execute(taskId)
                onSuccess("Task deleted successfully!")
            }
        }catch (e : Exception){
            onError(e.message ?: "Failed to delete task!")
        }
    }

}
