package dev.sudhanshu.taskmanager.presentation.view.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    id: String,
    taskId: String? = null,
    onTaskSaved: () -> Unit,
    onBack: () -> Unit,
    onSelectLocation: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(Timestamp.now()) }
    var priority by remember { mutableStateOf("Low") }
    var location by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(taskId) {
        taskId?.let { it ->
            viewModel.getTaskById(it,
                onSuccess = { task ->
                    task?.let {
                        title = it.title
                        description = it.description
                        dueDate = it.dueDate!!
                        priority = it.priority
                        location = it.location
                    }
                },
                onFailure = { /* Handle failure */ }
            )
        }
    }

    val priorities = listOf("High", "Medium", "Low")

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = remember { mutableStateOf(dateFormatter.format(dueDate.toDate())) }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            dueDate = Timestamp(calendar.time)
            formattedDate.value = dateFormatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    val bottomSheetState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = {
            MapScreen { selectedGeoPoint ->
                location = selectedGeoPoint
                coroutineScope.launch {
                    bottomSheetState.bottomSheetState.collapse()
                }
            }
        },
        sheetPeekHeight = 0.dp
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (taskId == null) "Add Task" else "Edit Task")},
                    modifier = Modifier.background(MaterialTheme.colors.background),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    backgroundColor = MaterialTheme.colors.background
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formattedDate.value,
                        onValueChange = { /* no-op */ },
                        label = { Text("Due Date") },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar),
                                contentDescription = "Select date",
                                modifier = Modifier.clickable {
                                    datePickerDialog.show()
                                }
                                    .padding(20.dp, 0.dp).size(24.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        readOnly = true
                    )
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = priority,
                            onValueChange = { /* no-op */ },
                            label = { Text("Priority") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Select Priority",
                                    modifier = Modifier.clickable { expanded = true }
                                        .padding(20.dp, 0.dp).size(24.dp)
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            priorities.forEach { priorityLevel ->
                                DropdownMenuItem(onClick = {
                                    priority = priorityLevel
                                    expanded = false
                                }) {
                                    Text(text = priorityLevel)
                                }
                            }
                        }
                    }
                    OutlinedTextField(
                        value = location?.toString() ?: "",
                        onValueChange = { /* no-op */ },
                        label = { Text("Location") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    bottomSheetState.bottomSheetState.expand()
                                }
                            },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.marker),
                                contentDescription = "Select Location",
                                modifier = Modifier.clickable {
                                    coroutineScope.launch {
                                        bottomSheetState.bottomSheetState.expand()
                                    }
                                }
                                    .padding(20.dp, 0.dp).size(24.dp)
                            )
                        }
                    )
                    Button(
                        onClick = {
                            if (taskId == null) {
                                val task = viewModel.addTask(
                                    title, description, dueDate, priority, location, id,
                                    onSuccess = {
                                        onTaskSaved()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show()
                                    }
                                )
                            } else {
                                viewModel.updateTask(
                                    Task(taskId, title, description, dueDate, priority, location, id),
                                    onSuccess = { onTaskSaved() },
                                    onFailure = { /* Handle failure */ }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 32.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colors.primary)
                            .align(Alignment.End)
                    ) {
                        Text(
                            text = if (taskId == null) "Add Task" else "Update Task",
                            color = Color.White,
                            style = MaterialTheme.typography.button
                        )
                    }
                }
            }
        )
    }
}
