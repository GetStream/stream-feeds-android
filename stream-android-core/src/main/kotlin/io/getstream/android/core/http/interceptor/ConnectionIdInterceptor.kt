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
package io.getstream.android.core.http.interceptor

import io.getstream.kotlin.base.annotation.marker.StreamInternalApi
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * An [Interceptor] that adds a connection ID as a query parameter to the request URL.
 *
 * This interceptor is used to track requests associated with a specific connection.
 *
 * @param connectionId A lambda that provides the connection ID to be added to the request URL.
 */
@StreamInternalApi
public class ConnectionIdInterceptor(private val connectionId: () -> String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the original request
        val request = chain.request()
        // Add connection ID as a query parameter if not empty
        val newRequest =
            connectionId().takeUnless(String::isEmpty)?.let { request.withConnectionId(it) }
                ?: request

        return chain.proceed(newRequest)
    }

    private fun Request.withConnectionId(connectionId: String): Request {
        // Build the new URL with connection ID as query parameter
        val urlWithConnectionId =
            url.newBuilder().addQueryParameter(QUERY_PARAM_CONNECTION_ID, connectionId).build()

        return newBuilder().url(urlWithConnectionId).build()
    }

    private companion object Companion {
        private const val QUERY_PARAM_CONNECTION_ID = "connection_id"
    }
}
