package dev.sudhanshu.taskmanager.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.domain.repository.TaskRepository
import com.google.android.gms.tasks.Task as GmsTask
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot

class TaskRepositoryImpl(private val db: FirebaseFirestore) : TaskRepository {

    override fun addTask(task: Task): GmsTask<Void> {
        return db.collection("tasks").document(task.id).set(task)
    }

    override fun updateTask(task: Task): GmsTask<Void> {
        return db.collection("tasks").document(task.id).set(task)
    }

    override fun getTask(taskId: String): GmsTask<Task> {
        return db.collection("tasks").document(taskId).get().continueWith { task ->
            task.result.toObject(Task::class.java)!!
        }
    }

    override fun deleteTask(taskId: String): GmsTask<Void> {
        return db.collection("tasks").document(taskId).delete()
    }

    override suspend fun getTasksForUser(userId: String): List<Task> {
        val querySnapshot = db.collection("tasks")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Task::class.java)
        }
    }
}


