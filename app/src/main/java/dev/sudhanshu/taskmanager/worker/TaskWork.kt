package dev.sudhanshu.taskmanager.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.domain.repository.TaskRepository
import dev.sudhanshu.taskmanager.util.LocationPreferences
import dev.sudhanshu.taskmanager.util.SettingsPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@HiltWorker
class TaskWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "TASK_CHANNEL"
        const val CHANNEL_NAME = "Task Notifications"
    }

    override suspend fun doWork(): Result {
        if (checkLocationPermission()) {
            val settingPref = SettingsPreferences(applicationContext)
            if (settingPref.areNotificationsEnabled()){
                val locationPreferences = LocationPreferences(applicationContext)
                val currentLocation = locationPreferences.getLocation() ?: return Result.failure()
                val id = locationPreferences.getUserId()
                fetchTasksAndNotify(currentLocation, applicationContext, id)
                return Result.success()
            }else return Result.success()
        } else {
            return Result.failure()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun fetchTasksAndNotify(
        currentLocation: Location,
        applicationContext: Context,
        id: String?
    ) {
        withContext(Dispatchers.IO) {
            try {
                val tasks = id?.let { taskRepository.getTasksForUser(it) }
                tasks?.filter { task ->
                    task.location?.let { taskLocation ->
                        val taskLocationObject = Location("").apply {
                            latitude = taskLocation.latitude
                            longitude = taskLocation.longitude
                        }
                        currentLocation.distanceTo(taskLocationObject) <= 1000 // 1 km radius
                    } ?: false
                }?.forEach { task ->
                    val distance = currentLocation.distanceTo(Location("").apply {
                        latitude = task.location!!.latitude
                        longitude = task.location!!.longitude
                    })
                    sendNotification(task, distance, applicationContext)
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun sendNotification(task: Task, distance: Float, applicationContext: Context) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Task Nearby")
            .setContentText("Task: ${task.title} is ${distance.roundToInt()} meters away.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        notificationManager.notify(task.id.hashCode(), notification)
    }


}
