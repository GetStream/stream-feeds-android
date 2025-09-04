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
import io.getstream.feeds.android.client.api.logging.Logger
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class CustomLoggerProviderTest(
    private val logLevel: StreamLogger.LogLevel,
    private val expectedLevel: Logger.Level,
) {
    @Test
    fun `on taggedLogger, return a logger that forwards the tag`() {
        val customLogger = mockk<Logger>(relaxed = true)
        val taggedLogger = CustomLoggerProvider(customLogger).taggedLogger("a tag")
        val exception = Exception("an exception")

        taggedLogger.log(logLevel, exception) { "a message" }

        verify {
            customLogger.log(
                level = expectedLevel,
                tag = "a tag",
                throwable = exception,
                message = match { it() == "a message" },
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> =
            listOf(
                arrayOf(StreamLogger.LogLevel.Verbose, Logger.Level.Verbose),
                arrayOf(StreamLogger.LogLevel.Debug, Logger.Level.Debug),
                arrayOf(StreamLogger.LogLevel.Info, Logger.Level.Info),
                arrayOf(StreamLogger.LogLevel.Warning, Logger.Level.Warning),
                arrayOf(StreamLogger.LogLevel.Error, Logger.Level.Error),
            )
    }
}
