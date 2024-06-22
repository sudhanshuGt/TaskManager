package dev.sudhanshu.taskmanager.domain.repository

import dev.sudhanshu.taskmanager.data.model.Task
import com.google.android.gms.tasks.Task as GmsTask

interface TaskRepository {
    fun addTask(task: Task): GmsTask<Void>
    fun updateTask(task: Task): GmsTask<Void>
    fun getTask(taskId: String): GmsTask<Task>
    fun deleteTask(taskId: String): GmsTask<Void>
    suspend fun getTasksForUser(userId: String): List<Task>
}