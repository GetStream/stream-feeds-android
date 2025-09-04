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
package io.getstream.feeds.android.client.internal.logging

import io.getstream.android.core.api.log.StreamLogger
import io.getstream.android.core.api.log.StreamLogger.LogLevel as CoreLogLevel
import io.getstream.android.core.api.log.StreamLoggerProvider
import io.getstream.feeds.android.client.api.logging.Logger

internal class CustomLoggerProvider(private val customLogger: Logger) : StreamLoggerProvider {
    override fun taggedLogger(tag: String): StreamLogger = CustomTaggedLogger(customLogger, tag)
}

private class CustomTaggedLogger(private val customLogger: Logger, private val tag: String) :
    StreamLogger {
    override fun log(level: CoreLogLevel, throwable: Throwable?, message: () -> String) =
        customLogger.log(level.map(), tag, throwable, message)
}

private fun CoreLogLevel.map(): Logger.Level =
    when (this) {
        CoreLogLevel.Verbose -> Logger.Level.Verbose
        CoreLogLevel.Debug -> Logger.Level.Debug
        CoreLogLevel.Info -> Logger.Level.Info
        CoreLogLevel.Warning -> Logger.Level.Warning
        CoreLogLevel.Error -> Logger.Level.Error
    }
