package dev.sudhanshu.taskmanager.presentation.view.component

import TaskCard
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.presentation.view.TaskDetailActivity
import dev.sudhanshu.taskmanager.presentation.view.AddEditTask
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskManagerViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.UserViewModel
import dev.sudhanshu.taskmanager.util.Resource

@Composable
fun TaskManagerScreen(
    viewModel: TaskManagerViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }

    // State to hold tasks and loading status
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    val userIdResource by userViewModel.user.collectAsState()
    val userId = when (userIdResource) {
        is Resource.Success -> (userIdResource as Resource.Success).data.id
        else -> ""
    }

    // State for search functionality
    var searchText by remember { mutableStateOf("") }

    // Function to fetch tasks
    fun fetchTasks() {
        isLoading = true
        viewModel.getTasksForUser(
            userId = userId,
            onSuccess = { fetchedTasks ->
                tasks = fetchedTasks
                isLoading = false
            },
            onError = {
                isLoading = false
                // Handle error if needed
            }
        )
    }

    // Fetching tasks and handle loading state
    LaunchedEffect(userId) {
        fetchTasks()
    }

    // Showing Snack bar if showSnackbar is true
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            scaffoldState.snackbarHostState.showSnackbar("Task deleted successfully")
            showSnackbar = false
        }
    }

    // Adding a LifecycleEventObserver to refresh the task list when the screen resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                fetchTasks()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Task Manager") },
                backgroundColor = MaterialTheme.colors.background,
                modifier = Modifier.background(MaterialTheme.colors.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddEditTask::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        padding.calculateTopPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp)
        ) {
            // Search box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {


                TextField(
                    value = searchText,
                    onValueChange = {searchText = it},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colors.background, textColor = MaterialTheme.colors.onBackground

                    ),
                    placeholder = {
                        Text("Search your task ")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 0.dp)
                        .heightIn(min = 56.dp)
                )

                // Clear button
                if (searchText.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                        modifier = Modifier
                            .clickable {
                                searchText = ""
                            }
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            // Task list
            if (isLoading) {
                // Showing loading indicator if tasks are being fetched
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (tasks.isEmpty()) {
                    // Showing no tasks available message with image
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
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
                } else {
                    // Filtering tasks based on search text
                    val filteredTasks = tasks.filter {
                        it.title.contains(searchText, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(filteredTasks) { task ->
                            TaskCard(
                                task = task,
                                onEdit = {
                                    val intent = Intent(context, AddEditTask::class.java)
                                    intent.putExtra("TASK_ID", it.id)
                                    context.startActivity(intent)
                                },
                                onDelete = { taskId ->
                                    viewModel.deleteUserTask(
                                        taskId,
                                        onSuccess = {
                                            // Refreshing the task list after successful deletion
                                            fetchTasks()
                                            showSnackbar = true
                                        },
                                        onError = {
                                            Toast.makeText(
                                                context,
                                                "Failed to delete task",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                },
                                onChecked = {
                                    task.isCompleted = it
                                    taskViewModel.updateTask(
                                        task,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Task status updated",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            fetchTasks()
                                        },
                                        onFailure = {

                                        })
                                },
                                onTaskClick = {
                                    val intent =
                                        Intent(context, TaskDetailActivity::class.java)
                                    intent.putExtra("TASK_ID", it.id)
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

