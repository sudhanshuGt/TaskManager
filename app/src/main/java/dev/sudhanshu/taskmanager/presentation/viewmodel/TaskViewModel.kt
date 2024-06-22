package dev.sudhanshu.taskmanager.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.domain.usecase.AddTaskUseCase
import dev.sudhanshu.taskmanager.domain.usecase.GetTaskUserCase
import dev.sudhanshu.taskmanager.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase?,
    private val updateTaskUseCase: UpdateTaskUseCase?,
    private val getTaskUseCase : GetTaskUserCase?
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    fun addTask(
        title: String,
        description: String,
        dueDate: Timestamp,
        priority: String,
        location: GeoPoint?,
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val task = Task(
                    id =  UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = priority,
                    location = location,
                    userId = userId
                )
                addTaskUseCase?.execute(task)?.await()
                onSuccess()
            } catch (e: Exception) {
                Log.i("--AddTaskViewModel--", e.toString())
                onFailure(e)
            }
        }
    }

    fun getTaskById(taskId: String, onSuccess: (Task?) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val task = getTaskUseCase?.execute(taskId)?.await()
                _task.value = task
                onSuccess(task)
            } catch (e: Exception) {
                Log.i("--AddTaskViewModel--", e.toString())
                onFailure(e)
            }
        }
    }

    fun updateTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                updateTaskUseCase?.execute(task)?.await()
                onSuccess()
            } catch (e: Exception) {
                Log.i("--AddTaskViewModel--", e.toString())
                onFailure(e)
            }
        }
    }
}
