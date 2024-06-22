package dev.sudhanshu.taskmanager.domain.usecase

import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    fun execute(task: Task) = repository.addTask(task)
}