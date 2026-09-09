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

package io.getstream.feeds.android.client.internal.http

import io.getstream.android.core.api.log.StreamLoggerProvider
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Returns an interceptor applying [customHeaders] to every API request, or null if none of them
 * survive: [RESERVED_HEADERS] are dropped with a warning.
 *
 * @throws IllegalArgumentException if a name or value is not valid HTTP, or if two names differ
 *   only in case, which HTTP treats as one header. The message names the header but never its
 *   value, which may be a secret.
 */
internal fun createCustomHeadersInterceptor(
    customHeaders: Map<String, String>,
    logProvider: StreamLoggerProvider,
): Interceptor? {
    if (customHeaders.isEmpty()) return null

    val logger = logProvider.taggedLogger("FeedCustomHeaders")
    val builder = Headers.Builder()
    customHeaders.forEach { (name, value) ->
        if (name.isReservedHeader()) {
            logger.w {
                "Ignoring custom header '$name': it is set by the SDK and cannot be overridden."
            }
        } else {
            require(builder.get(name) == null) {
                "Duplicate entry in FeedsConfig.customHeaders: '$name' differs only in case from a " +
                    "name already supplied, and HTTP treats the two as one header."
            }
            require(runCatching { builder.set(name, value) }.isSuccess) {
                "Invalid entry in FeedsConfig.customHeaders for name '$name'. Header names and " +
                    "values must be valid HTTP: visible ASCII, no line breaks."
            }
        }
    }
    return builder.build().takeIf { it.size > 0 }?.let(::CustomHeadersInterceptor)
}

/**
 * Applies [headers] to every API request.
 *
 * Private so that [createCustomHeadersInterceptor] is the only way to get one, and the headers it
 * holds have always been through the checks there.
 */
private class CustomHeadersInterceptor(private val headers: Headers) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        for (i in 0 until headers.size) {
            builder.header(headers.name(i), headers.value(i))
        }
        return chain.proceed(builder.build())
    }
}

/**
 * Headers the SDK or OkHttp controls, which callers must not override.
 *
 * The first three come from `stream-core-android`'s interceptors, registered via
 * `StreamHttpConfig.automaticInterceptors`. The rest are OkHttp's, which sets them only when absent
 * or when the request has a body, so a caller could otherwise take them over: overriding
 * `Accept-Encoding` stops it decompressing responses, `Host` changes routing, `Connection` breaks
 * connection pooling, and the framing headers corrupt a bodiless request. `User-Agent` and `Cookie`
 * are deliberately left overridable.
 */
private val RESERVED_HEADERS =
    listOf(
        "Authorization",
        "stream-auth-type",
        "X-Stream-Client",
        "Content-Type",
        "Content-Length",
        "Transfer-Encoding",
        "Connection",
        "Accept-Encoding",
        "Host",
    )

private fun String.isReservedHeader() = RESERVED_HEADERS.any { it.equals(this, ignoreCase = true) }
