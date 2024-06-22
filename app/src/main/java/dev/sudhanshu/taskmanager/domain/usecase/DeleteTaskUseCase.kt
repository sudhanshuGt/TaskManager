package dev.sudhanshu.taskmanager.domain.usecase

import dev.sudhanshu.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    fun execute(taskId: String) = repository.deleteTask(taskId)
}