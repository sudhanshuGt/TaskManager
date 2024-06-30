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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

    val coroutineScope = rememberCoroutineScope()
    var resetSwipeState by remember { mutableStateOf(false) }

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
                },
                backgroundColor = Color(0xFFE5FF7F)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.Black)
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
            Spacer(modifier = Modifier.height(10.dp))
            SearchTextField(searchText = searchText, onValueChange = {
                searchText = it
            })
            Spacer(modifier = Modifier.height(10.dp))


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
                            .nestedScroll(object : NestedScrollConnection {
                                override fun onPreScroll(
                                    available: Offset,
                                    source: NestedScrollSource
                                ): Offset {
                                    if (resetSwipeState) {
                                        resetSwipeState = false
                                    }
                                    return Offset.Zero
                                }
                            })
                    ) {
                        items(filteredTasks) { task ->
                            SwipeToDismissTaskItem(
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
                                onTaskClicked = {
                                    val intent = Intent(context, TaskDetailActivity::class.java)
                                    intent.putExtra("TASK_ID", it.id)
                                    context.startActivity(intent)
                                },
                                onReset = {
                                    resetSwipeState = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchTextField(searchText: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = searchText,
        onValueChange = { onValueChange(it) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon"
            )
        },
        placeholder = {
            Text("Search your task")
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colors.onBackground,
            unfocusedBorderColor = MaterialTheme.colors.onBackground,
            textColor = MaterialTheme.colors.onBackground,
            backgroundColor = MaterialTheme.colors.background,
            cursorColor = MaterialTheme.colors.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(20.dp)
    )
}

