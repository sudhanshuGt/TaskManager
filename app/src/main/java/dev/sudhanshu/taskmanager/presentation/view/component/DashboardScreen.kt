package dev.sudhanshu.taskmanager.presentation.view.component

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.presentation.ui.theme.Typography
import dev.sudhanshu.taskmanager.presentation.view.AddEditTask
import dev.sudhanshu.taskmanager.presentation.view.TaskDetailActivity
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskManagerViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.UserViewModel
import dev.sudhanshu.taskmanager.util.Resource
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    taskViewModel: TaskManagerViewModel = hiltViewModel(),
    userViewModel : UserViewModel  = hiltViewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val tasks by taskViewModel.task.collectAsState()

    val userIdResource by userViewModel.user.collectAsState()
    val userId = when (userIdResource) {
        is Resource.Success -> (userIdResource as Resource.Success).data.id
        else -> ""
    }

    LaunchedEffect(Unit) {
        taskViewModel.getTasksForUser(userId, onSuccess = {

        }, onError = {

        })
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Dashboard") },
                modifier = Modifier.background(MaterialTheme.colors.background),
                backgroundColor = MaterialTheme.colors.background
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    if (!tasks.isNullOrEmpty()) tasks?.let { TaskStatisticsSection(it) }
                }
                item {
                    if(!tasks.isNullOrEmpty()) tasks?.let {
                        UpcomingTasksSection(it, onClick = { task ->
                        val intent = Intent(context, TaskDetailActivity::class.java)
                        intent.putExtra("TASK_ID", task.id)
                        context.startActivity(intent)
                    }) }
                }
                item {
                    if(!tasks.isNullOrEmpty()) tasks?.let { PendingTasksSection(it, onClick = { task ->
                        val intent = Intent(context, TaskDetailActivity::class.java)
                        intent.putExtra("TASK_ID", task.id)
                        context.startActivity(intent)
                    }) }
                }
                item {
                    if(!tasks.isNullOrEmpty()) tasks?.let { CompletedTasksSection(it, onClick = { task ->
                        val intent = Intent(context, TaskDetailActivity::class.java)
                        intent.putExtra("TASK_ID", task.id)
                        context.startActivity(intent)
                    }) }
                }

                item {
                    if (tasks.isNullOrEmpty()) NoTaskMessage()
                }
            }
        }
    )
}

@Composable
fun TaskStatisticsSection(tasks: List<Task>) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Task Statistics", fontSize = 18.sp, style = Typography.h1)
        Spacer(modifier = Modifier.height(10.dp))
        TaskPriorityPieChart(tasks = tasks, modifier = Modifier
            .height(200.dp)
            .width(200.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpcomingTasksSection(tasks: List<Task>, onClick: (Task) -> Unit) {
    val today = LocalDate.now()
    val upcomingTasks = tasks.filter { task ->
        val dueDate = task.dueDate?.toDate()
        dueDate?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()?.isAfter(today) == true
    }.sortedBy { it.dueDate?.toDate() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Upcoming Tasks", fontSize = 18.sp, style = Typography.h1)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow {
            items(upcomingTasks.filter {
                task -> !task.isCompleted
            }) { task ->
                TaskItem(task, onClick = {
                    onClick(task)
                }, onDeleteClick = {
                    onClick(task)
                }, onEditClick = {
                    onClick(task)
                })
            }
        }
    }
}


@Composable
fun PendingTasksSection(tasks: List<Task>, onClick: (Task) -> Unit) {
    val pendingTasks = tasks.filter { !it.isCompleted }
        .sortedBy { it.dueDate?.toDate() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Pending Tasks", fontSize = 18.sp, style = Typography.h1)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow {
            items(pendingTasks) { task ->
                TaskItem(task, onClick = {

                }, onDeleteClick = {
                    onClick(task)
                }, onEditClick = {
                    onClick(task)
                })
            }
        }
    }
}

@Composable
fun CompletedTasksSection(tasks: List<Task>, onClick: (Task) -> Unit) {
    val pendingTasks = tasks.filter { it.isCompleted }
        .sortedBy { it.dueDate?.toDate() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Completed Tasks", fontSize = 18.sp, style = Typography.h1)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow {
            items(pendingTasks) { task ->
                TaskItem(task, onClick = {
                    onClick(task)
                }, onDeleteClick = {
                    onClick(task)
                }, onEditClick = {
                    onClick(task)
                })
            }
        }
    }
}

@Composable
fun NoTaskMessage(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.no_task),
                contentDescription = "No Tasks",
                modifier = Modifier.size(128.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tasks available",
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
        }
    }
}



@Composable
fun TaskItem(task: Task, onClick: (Task) -> Unit, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(10.dp)
            .width(250.dp)
            .clickable { onClick(task) }
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = task.title,
                style = Typography.h2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Date",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatDueDate(task.dueDate),
                    style = Typography.h3,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.priority_arrow),
                    contentDescription = "Priority",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = task.priority,
                    style = Typography.h3,
                    color = priorityColor(priority = task.priority),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete Icon",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.onSecondary)
                        .padding(4.dp)
                        .clickable { onDeleteClick() }
                )

                Spacer(modifier = Modifier.width(10.dp))

                if (!task.isCompleted) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit Icon",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.onSecondary)
                            .padding(4.dp)
                            .clickable { onEditClick() }
                    )
                }
            }
        }
    }


}




@SuppressLint("NewApi")
private fun formatDueDate(dueDate: Timestamp?): String {
    dueDate ?: return "Not specified"

    val instant = dueDate.toDate().toInstant()
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, hh:mm a")

    return dateTime.format(formatter)
}
