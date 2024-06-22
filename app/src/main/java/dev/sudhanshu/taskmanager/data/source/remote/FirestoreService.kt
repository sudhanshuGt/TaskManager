package dev.sudhanshu.taskmanager.data.source.remote



import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreService(private val firestore: FirebaseFirestore) {

    suspend fun saveUser(userId: String, userData: Map<String, Any>) {
        firestore.collection("users").document(userId).set(userData).await()
    }

    suspend fun getUser(userId: String) = firestore.collection("users").document(userId).get().await()
}
