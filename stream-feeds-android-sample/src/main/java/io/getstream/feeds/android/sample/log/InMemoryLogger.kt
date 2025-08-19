/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    val timestamp: Long = System.currentTimeMillis(),
)

class InMemoryLogger : StreamLogger {

    companion object {
        private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
        val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()
    }

    override fun log(priority: Priority, tag: String, message: String, throwable: Throwable?) {
        val entry = LogEntry(priority, tag, message, throwable)
        _logs.value = _logs.value + entry
    }
}
