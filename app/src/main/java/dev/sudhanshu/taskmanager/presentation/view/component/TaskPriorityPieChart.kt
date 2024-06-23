package dev.sudhanshu.taskmanager.presentation.view.component



import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dev.sudhanshu.taskmanager.data.model.Task

@Composable
fun TaskPriorityPieChart(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    // Calculating the counts for each priority level
    val highPriorityCount = tasks.count { it.priority.equals("High", ignoreCase = true) }
    val mediumPriorityCount = tasks.count { it.priority.equals("Medium", ignoreCase = true) }
    val lowPriorityCount = tasks.count { it.priority.equals("Low", ignoreCase = true) }

    // Preparing the PieEntries
    val entries = listOf(
        if (highPriorityCount >= 1) PieEntry(highPriorityCount.toFloat(), "High") else PieEntry(0f, "High"),
        if(mediumPriorityCount >= 1) PieEntry(mediumPriorityCount.toFloat(), "Medium") else PieEntry(0f, "Medium"),
        if(lowPriorityCount >= 1) PieEntry(lowPriorityCount.toFloat(), "Low") else PieEntry(0f, "Low")
    )

    // Creating the PieDataSet and PieData
    val dataSet = PieDataSet(entries, "Task Priorities")
    dataSet.colors = listOf(Color.RED, Color.GREEN, Color.GRAY)
    val pieData = PieData(dataSet)

    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                data = pieData
                description.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 50f
                setEntryLabelColor(Color.BLACK)
                setEntryLabelTextSize(8f)
                legend.isEnabled = false
                invalidate()
            }
        },
        modifier = modifier
    )
}
