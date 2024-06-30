import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.ui.theme.Typography
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskCard(
    task: Task,
    onEdit: (Task) -> Unit,
    onDelete: (String) -> Unit,
    onChecked: (Boolean) -> Unit,
    onTaskClick : (Task) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberDismissState()

    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        coroutineScope.launch {
            dismissState.snapTo( DismissValue.DismissedToStart)
        }
    }

    TaskManagerTheme {
        Box(modifier = Modifier
            .padding(20.dp, 10.dp)
            .background(MaterialTheme.colors.background)
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                onTaskClick(task)
            }) {



            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                dismissThresholds = { FractionalThreshold(0.5f) },
                background = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),

                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if(!task.isCompleted){
                                Column(
                                    modifier = Modifier
                                        .background(Color.Green)
                                        .padding(30.dp)
                                        .fillMaxHeight()
                                        .clickable {
                                            onEdit(task)
                                            coroutineScope.launch { dismissState.reset() }
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Edit",
                                        color = Color.White,
                                        style = Typography.body2
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .background(Color.Red)
                                    .padding(30.dp)
                                    .fillMaxHeight()
                                    .clickable {
                                        onDelete(task.id)
                                        coroutineScope.launch { dismissState.reset() }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Delete",
                                    color = Color.White,
                                    style = Typography.body2
                                )
                            }
                        }
                    }
                },
                dismissContent = {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colors.background,
                        modifier = Modifier
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp, 10.dp)
                        ) {
                            Text(
                                text = task.title,
                                style = Typography.h1,
                                fontSize = 18.sp
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
                            Row(
                                modifier = Modifier.align(Alignment.End),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = {
                                        if(!task.isCompleted){
                                            onChecked(!task.isCompleted)
                                        }
                                    },
                                    modifier = Modifier.padding(end = 2.dp)
                                )
                                Text(
                                    text = if (task.isCompleted) "Task Completed" else "Task Uncompleted",
                                    style = Typography.h6,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun priorityColor(priority: String): Color {
    return when (priority.toLowerCase(Locale.ROOT)) {
        "high" -> Color.Red
        "medium" -> Color.Green
        "low" -> Color.Gray
        else -> Color.LightGray
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
