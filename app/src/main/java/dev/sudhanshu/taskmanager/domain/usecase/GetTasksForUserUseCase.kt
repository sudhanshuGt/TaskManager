package dev.sudhanshu.taskmanager.domain.usecase


import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class GetTasksForUserUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    suspend operator fun invoke(userId: String): List<Task> {
        return taskRepository.getTasksForUser(userId)
    }
}
