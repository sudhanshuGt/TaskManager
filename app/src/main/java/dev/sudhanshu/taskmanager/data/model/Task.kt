package dev.sudhanshu.taskmanager.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Task(
    @DocumentId val id: String = "",
    var title: String = "",
    var description: String = "",
    var dueDate: Timestamp? = null,
    var priority: String = "",
    var location: GeoPoint? = null,
    var userId: String = "",
    var isCompleted : Boolean = false
)

