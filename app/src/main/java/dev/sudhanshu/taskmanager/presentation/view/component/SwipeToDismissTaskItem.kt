package dev.sudhanshu.taskmanager.presentation.view.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.data.model.Task
import dev.sudhanshu.taskmanager.presentation.ui.theme.Typography
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissTaskItem(
    task: Task,
    onEdit: (Task) -> Unit,
    onDelete: (String) -> Unit,
    onChecked: (Boolean) -> Unit,
    onTaskClicked: (Task) -> Unit,
    onReset: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(0f to 0, if(task.isCompleted) -300f to 1 else -600f to 1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .pointerInput(Unit) {
                detectTapGestures {
                    if (swipeableState.currentValue == 1) {
                        coroutineScope.launch {
                            swipeableState.snapTo(0)
                            onReset()
                        }
                    }
                }
            }
            .background(Color.Transparent),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(150.dp)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!task.isCompleted) {
                Column(
                    modifier = Modifier
                        .background(Color.Green)
                        .fillMaxHeight()
                        .padding(horizontal = 30.dp, vertical = 50.dp)
                        .clickable {
                            onEdit(task)
                            coroutineScope.launch { swipeableState.snapTo(0) }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
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
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp, vertical = 50.dp)
                    .clickable {
                        onDelete(task.id)
                        coroutineScope.launch { swipeableState.snapTo(0) }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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

        Card(
            shape = RoundedCornerShape(0.dp),
            elevation = 2.dp,
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .fillMaxWidth().clickable {
                    if(swipeableState.currentValue > 0){
                        coroutineScope.launch { swipeableState.snapTo(0) }
                    }else{
                        onTaskClicked(task)
                    }
                }.heightIn(150.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp, 10.dp)
            ) {
                Text(
                    text = task.title,
                    style = Typography.h1,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onBackground
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
                            if (!task.isCompleted) {
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
}


@SuppressLint("NewApi")
private fun formatDueDate(dueDate: Timestamp?): String {
    dueDate ?: return "Not specified"

    val instant = dueDate.toDate().toInstant()
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, hh:mm a")

    return dateTime.format(formatter)
}
