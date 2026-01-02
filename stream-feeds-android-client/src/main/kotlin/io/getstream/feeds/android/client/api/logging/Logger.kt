/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

package io.getstream.feeds.android.client.api.logging

/** A logger interface for logging messages at different levels. */
public interface Logger {
    /**
     * Logs a message at the specified level with an optional throwable.
     *
     * @param level The log level.
     * @param tag A tag to identify the source of the log message.
     * @param throwable An optional throwable associated with the log message.
     * @param message A lambda that produces the log message.
     */
    public fun log(level: Level, tag: String, throwable: Throwable? = null, message: () -> String)

    public enum class Level {
        Verbose,
        Debug,
        Info,
        Warning,
        Error,
    }
}
