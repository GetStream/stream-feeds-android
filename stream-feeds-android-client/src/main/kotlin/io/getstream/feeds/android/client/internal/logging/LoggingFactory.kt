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

import io.getstream.android.core.api.log.StreamLoggerProvider
import io.getstream.feeds.android.client.api.logging.HttpLoggingLevel
import io.getstream.feeds.android.client.api.logging.Logger
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

internal fun createLoggerProvider(customLogger: Logger?): StreamLoggerProvider =
    customLogger?.let(::FeedsLoggerProvider) ?: StreamLoggerProvider.defaultAndroidLogger()

internal fun createLoggingInterceptor(
    provider: StreamLoggerProvider,
    level: HttpLoggingLevel,
): Interceptor {
    val logger = provider.taggedLogger("FeedHTTP")

    return HttpLoggingInterceptor(logger = { logger.i { it } }).setLevel(level.toOkHttpLevel())
}

private fun HttpLoggingLevel.toOkHttpLevel(): HttpLoggingInterceptor.Level =
    when (this) {
        HttpLoggingLevel.None -> HttpLoggingInterceptor.Level.NONE
        HttpLoggingLevel.Basic -> HttpLoggingInterceptor.Level.BASIC
        HttpLoggingLevel.Headers -> HttpLoggingInterceptor.Level.HEADERS
        HttpLoggingLevel.Body -> HttpLoggingInterceptor.Level.BODY
    }
