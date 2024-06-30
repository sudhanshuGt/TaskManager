package dev.sudhanshu.taskmanager.presentation.view.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Typography
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.presentation.ui.theme.ThemeManager
import dev.sudhanshu.taskmanager.presentation.ui.theme.Typography
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(MaterialTheme.colors.background)
            ) {
                MapScreen { selectedGeoPoint ->
                    location = selectedGeoPoint
                    coroutineScope.launch {
                        bottomSheetState.bottomSheetState.collapse()
                    }
                }

            }
        },
        sheetPeekHeight = 0.dp
    ) {
        Scaffold(
            content = { padding ->
              Box(modifier = Modifier
                  .fillMaxSize()
                  .padding(padding)){
                  Card (modifier = Modifier.padding(10.dp).wrapContentHeight(),
                      backgroundColor = Color.White,
                      shape = RoundedCornerShape(20.dp),
                      elevation = 4.dp){
                      Column(
                          modifier = Modifier
                              .padding(padding)
                              .padding(16.dp, 20.dp)
                              .wrapContentHeight()
                              .background(Color.White),
                          verticalArrangement = Arrangement.spacedBy(20.dp)
                      ) {

                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.SpaceBetween,
                              modifier = Modifier.fillMaxWidth()
                          ) {
                              IconButton(onClick = onBack) {
                                  Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                              }
                              Text(
                                  text = if (taskId == null) "Add Task" else "Edit Task",
                                  modifier = Modifier.align(Alignment.CenterVertically),
                                  style = Typography.h2,
                                  color = Color.Black,
                                  fontSize = 18.sp
                              )
                              Spacer(modifier = Modifier.width(48.dp))
                          }

                          OutlinedTextField(
                              value = title,
                              onValueChange = { title = it },
                              label = { Text("Title", color = Color.Black) },
                              maxLines = 1,
                              shape = RoundedCornerShape(20.dp),
                              colors = TextFieldDefaults.outlinedTextFieldColors(
                                  textColor = Color.Black,
                                  cursorColor = Color.Black,
                                  focusedBorderColor = Color.Black,
                                  unfocusedBorderColor = Color.Black
                              ),
                              modifier = Modifier
                                  .fillMaxWidth()
                                  .height(60.dp)
                          )

                          OutlinedTextField(
                              value = description,
                              onValueChange = { description = it },
                              label = { Text("Description", color = Color.Black) },
                              maxLines = 1,
                              shape = RoundedCornerShape(20.dp),
                              colors = TextFieldDefaults.outlinedTextFieldColors(
                                  textColor = Color.Black,
                                  cursorColor = Color.Black,
                                  focusedBorderColor = Color.Black,
                                  unfocusedBorderColor = Color.Black
                              ),
                              modifier = Modifier
                                  .fillMaxWidth()
                                  .height(60.dp)
                          )

                          Row(
                              modifier = Modifier.fillMaxWidth(),
                              horizontalArrangement = Arrangement.spacedBy(16.dp) // Adjust the spacing as needed
                          ) {
                              OutlinedTextField(
                                  value = formattedDate.value,
                                  onValueChange = { /* no-op */ },
                                  label = { Text("Due Date", color = Color.Black) },
                                  maxLines = 1,
                                  shape = RoundedCornerShape(20.dp),
                                  trailingIcon = {
                                      Icon(
                                          painter = painterResource(id = R.drawable.calendar),
                                          contentDescription = "Select date",
                                          tint = Color.Black,
                                          modifier = Modifier
                                              .clickable {
                                                  datePickerDialog.show()
                                              }
                                              .padding(20.dp, 0.dp)
                                              .size(24.dp)
                                      )
                                  },
                                  colors = TextFieldDefaults.outlinedTextFieldColors(
                                      textColor = Color.Black,
                                      cursorColor = Color.Black,
                                      focusedBorderColor = Color.Black,
                                      unfocusedBorderColor = Color.Black
                                  ),
                                  modifier = Modifier
                                      .weight(1f)
                                      .height(60.dp)
                                      .clickable { datePickerDialog.show() },
                                  readOnly = true
                              )

                              var expanded by remember { mutableStateOf(false) }
                              Box(modifier = Modifier
                                  .weight(1f)
                              ) {
                                  OutlinedTextField(
                                      value = priority,
                                      onValueChange = { /* no-op */ },
                                      label = { Text("Priority", color = Color.Black) },
                                      maxLines = 1,
                                      shape = RoundedCornerShape(20.dp),
                                      trailingIcon = {
                                          Icon(
                                              Icons.Default.ArrowBack,
                                              contentDescription = "Select Priority",
                                              tint = Color.Black,
                                              modifier = Modifier
                                                  .clickable { expanded = true }
                                                  .padding(20.dp, 0.dp)
                                                  .size(24.dp)
                                          )
                                      },
                                      colors = TextFieldDefaults.outlinedTextFieldColors(
                                          textColor = Color.Black,
                                          cursorColor = Color.Black,
                                          focusedBorderColor = Color.Black,
                                          unfocusedBorderColor = Color.Black
                                      ),
                                      modifier = Modifier
                                          .fillMaxWidth()
                                          .height(60.dp)
                                          .clickable { expanded = true },
                                      readOnly = true
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
                          }

                          OutlinedTextField(
                              value = location?.toString() ?: "",
                              onValueChange = { /* no-op */ },
                              label = { Text("Location", color = Color.Black) },
                              maxLines = 1,
                              shape = RoundedCornerShape(20.dp),
                              trailingIcon = {
                                  Icon(
                                      painter = painterResource(id = R.drawable.marker),
                                      contentDescription = "Select Location",
                                      tint = Color.Black,
                                      modifier = Modifier
                                          .clickable {
                                              coroutineScope.launch {
                                                  bottomSheetState.bottomSheetState.expand()
                                              }
                                          }
                                          .padding(20.dp, 0.dp)
                                          .size(24.dp)
                                  )
                              },
                              colors = TextFieldDefaults.outlinedTextFieldColors(
                                  textColor = Color.Black,
                                  cursorColor = Color.Black,
                                  focusedBorderColor = Color.Black,
                                  unfocusedBorderColor = Color.Black
                              ),
                              modifier = Modifier
                                  .fillMaxWidth()
                                  .height(60.dp)
                                  .clickable {
                                      coroutineScope.launch {
                                          bottomSheetState.bottomSheetState.expand()
                                      }
                                  },
                              readOnly = true
                          )
                      }

                  }



                  Button(
                      onClick = {
                          if (taskId == null ) {
                              if(title.isNotEmpty()){
                                  viewModel.addTask(
                                      title, description, dueDate, priority, location, id,
                                      onSuccess = { onTaskSaved() },
                                      onFailure = {
                                          Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show()
                                      }
                                  )
                              }

                          } else {
                             if(title.isNotEmpty()){
                                 viewModel.updateTask(
                                     Task(taskId, title, description, dueDate, priority, location, id),
                                     onSuccess = { onTaskSaved() },
                                     onFailure = { /* Handle failure */ }
                                 )
                             }
                          }
                      },
                      modifier = Modifier
                          .align(Alignment.BottomCenter)
                          .padding(16.dp)
                          .clip(RoundedCornerShape(20.dp))
                          .height(66.dp)
                          .fillMaxWidth(),
                      colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE5FF7F)),
                      elevation = ButtonDefaults.elevation(4.dp)
                  ) {
                      Text(
                          text = if (taskId == null) "Add Task" else "Update Task",
                          style = Typography.h1,
                          color = Color.Black,
                          fontSize = 16.sp
                      )
                  }
              }
            }
        )
    }
}
