package io.getstream.feeds.android.sample.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.getstream.feeds.android.sample.log.InMemoryLogger
import io.getstream.feeds.android.sample.log.LogEntry
import io.getstream.log.Priority
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen() {
    val logs by InMemoryLogger.logs.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (logs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No logs available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(logs.reversed()) { logEntry ->
                    LogEntryCard(logEntry = logEntry)
                }
            }
        }
    }
}

@Composable
private fun LogEntryCard(logEntry: LogEntry) {
    val backgroundColor = getPriorityBackgroundColor(logEntry.priority)
    val textColor = getPriorityTextColor(logEntry.priority)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header with timestamp and priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimestamp(logEntry.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f)
                )
                
                PriorityChip(priority = logEntry.priority)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Tag
            Text(
                text = logEntry.tag,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Message
            Text(
                text = logEntry.message,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = textColor
            )
            
            // Throwable (if present)
            logEntry.throwable?.let { throwable ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Exception: ${throwable.javaClass.simpleName}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.Red
                )
                Text(
                    text = throwable.message ?: "No message",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Red.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PriorityChip(priority: Priority) {
    val chipColor = getPriorityChipColor(priority)
    val chipTextColor = Color.White
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = chipColor,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = priority.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = chipTextColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun getPriorityBackgroundColor(priority: Priority): Color {
    return when (priority.name.uppercase()) {
        "VERBOSE" -> Color(0xFFF5F5F5)
        "DEBUG" -> Color(0xFFE3F2FD)
        "INFO" -> Color(0xFFE8F5E8)
        "WARN" -> Color(0xFFFFF3E0)
        "ERROR" -> Color(0xFFFFEBEE)
        "ASSERT" -> Color(0xFFFFE0E6)
        else -> MaterialTheme.colorScheme.surface
    }
}

@Composable
private fun getPriorityTextColor(priority: Priority): Color {
    return when (priority.name.uppercase()) {
        "VERBOSE" -> Color(0xFF616161)
        "DEBUG" -> Color(0xFF1976D2)
        "INFO" -> Color(0xFF388E3C)
        "WARN" -> Color(0xFFF57C00)
        "ERROR" -> Color(0xFFD32F2F)
        "ASSERT" -> Color(0xFFAD1457)
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
private fun getPriorityChipColor(priority: Priority): Color {
    return when (priority.name.uppercase()) {
        "VERBOSE" -> Color(0xFF9E9E9E)
        "DEBUG" -> Color(0xFF2196F3)
        "INFO" -> Color(0xFF4CAF50)
        "WARN" -> Color(0xFFFF9800)
        "ERROR" -> Color(0xFFF44336)
        "ASSERT" -> Color(0xFFE91E63)
        else -> Color(0xFF757575)
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return sdf.format(Date(timestamp))
}