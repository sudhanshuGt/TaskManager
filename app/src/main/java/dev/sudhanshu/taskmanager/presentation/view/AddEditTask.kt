package dev.sudhanshu.taskmanager.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import dev.sudhanshu.taskmanager.presentation.view.component.AddEditTaskScreen
import dev.sudhanshu.taskmanager.presentation.view.component.MapScreen
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.viewmodel.TaskViewModel
import dev.sudhanshu.taskmanager.presentation.viewmodel.UserViewModel
import dev.sudhanshu.taskmanager.util.Resource
import dev.sudhanshu.taskmanager.util.SettingsPreferences
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditTask : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()


    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId = intent.getStringExtra("TASK_ID")

        setContent {
            TaskManagerTheme {
                val userIdResource by userViewModel.user.collectAsState()
                val userId = when (userIdResource) {
                    is Resource.Success -> (userIdResource as Resource.Success).data.id
                    else -> ""
                }

                val scaffoldState = rememberBottomSheetScaffoldState()
                val scope = rememberCoroutineScope()

                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetContent = {
                        MapScreen(onLocationSelected = {
                            // Handle the selected location
                            scope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        })
                    },
                    sheetPeekHeight = 0.dp,
                ) {
                    AddEditTaskScreen(
                        taskId = taskId,
                        id = userId,
                        viewModel = taskViewModel,
                        onTaskSaved = { finish() },
                        onBack = { finish() },
                        onSelectLocation = {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    )
                }
            }
        }
    }
}
