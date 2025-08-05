package io.getstream.feeds.android.sample.log

import io.getstream.log.Priority
import io.getstream.log.StreamLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LogEntry(
    val priority: Priority,
    val tag: String,
    val message: String,
    val throwable: Throwable?,
    val timestamp: Long = System.currentTimeMillis()
)

class InMemoryLogger: StreamLogger {
    
    companion object {
        private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
        val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()
    }
    
    override fun log(
        priority: Priority,
        tag: String,
        message: String,
        throwable: Throwable?
    ) {
        val entry = LogEntry(priority, tag, message, throwable)
        _logs.value = _logs.value + entry
    }
}