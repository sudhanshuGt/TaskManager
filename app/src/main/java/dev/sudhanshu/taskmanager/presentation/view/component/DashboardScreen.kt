package dev.sudhanshu.taskmanager.presentation.view.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.presentation.ui.theme.Typography
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskManagerViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.UserViewModel
import dev.sudhanshu.taskmanager.util.Resource
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
                    tasks?.let { TaskStatisticsSection(it) }
                }
                item {
                    tasks?.let { UpcomingTasksSection(it) }
                }
                item {
                    tasks?.let { PendingTasksSection(it) }
                }
                item {
                    tasks?.let { CompletedTasksSection(it) }
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

@Composable
fun UpcomingTasksSection(tasks: List<Task>) {
    val upcomingTasks = tasks.filter { !it.isCompleted }
        .sortedBy { it.dueDate?.toDate() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Upcoming Tasks", fontSize = 18.sp, style = Typography.h1)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow {
            items(upcomingTasks) { task ->
                TaskItem(task)
            }
        }
    }
}


@Composable
fun PendingTasksSection(tasks: List<Task>) {
    val pendingTasks = tasks.filter { !it.isCompleted }
        .sortedBy { it.dueDate?.toDate() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Pending Tasks", fontSize = 18.sp, style = Typography.h1)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow {
            items(pendingTasks) { task ->
                TaskItem(task)
            }
        }
    }
}

@Composable
fun CompletedTasksSection(tasks: List<Task>) {
    val pendingTasks = tasks.filter { it.isCompleted }
        .sortedBy { it.dueDate?.toDate() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Completed Tasks", fontSize = 18.sp, style = Typography.h1)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow {
            items(pendingTasks) { task ->
                TaskItem(task)
            }
        }
    }
}



@Composable
fun TaskItem(task: Task) {
    Card(
        shape = RoundedCornerShape(6.dp),
        elevation = 4.dp,
        modifier = Modifier.padding(10.dp, 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp, 6.dp)
        ) {
            Text(
                text = task.title,
                style = Typography.h2,
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
