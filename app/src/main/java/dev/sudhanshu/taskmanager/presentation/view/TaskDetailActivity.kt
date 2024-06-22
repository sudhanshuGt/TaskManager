package dev.sudhanshu.taskmanager.presentation.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.view.component.TaskDetailScreen

@AndroidEntryPoint
class TaskDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskId = intent.getStringExtra("TASK_ID")
        setContent {
            TaskManagerTheme {
                if (taskId != null) {
                    TaskDetailScreen(taskId =taskId ,
                        onTaskDelete = {
                        finish()
                    }, onBack = {
                        finish()
                        }, onEditTask = {
                            val intent = Intent(this, AddEditTask::class.java)
                            intent.putExtra("TASK_ID", it.id)
                            startActivity(intent)
                        }) 
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskManagerTheme {
        TaskDetailScreen(taskId = "", onTaskDelete = { /*TODO*/ }, onBack = { /*TODO*/ }) {
            
        }
    }
}