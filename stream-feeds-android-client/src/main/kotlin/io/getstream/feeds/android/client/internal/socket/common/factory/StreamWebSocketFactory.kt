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
package io.getstream.feeds.android.client.internal.socket.common.factory

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.internal.log.provideLogger
import io.getstream.log.TaggedLogger
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Factory for creating WebSocket connections.
 *
 * @param okHttpClient The OkHttpClient instance to use for creating WebSocket connections.
 */
internal class StreamWebSocketFactory(
    private val okHttpClient: OkHttpClient = OkHttpClient(),
    private val logger: TaggedLogger = provideLogger(tag = "StreamWebSocketFactory"),
) {

    internal fun createSocket(
        config: StreamSocketConfig,
        listener: WebSocketListener,
    ): Result<WebSocket> = runSafely {
        logger.v { "[createSocket] config: $config" }
        val request = buildRequest(config)
        okHttpClient.newWebSocket(request, listener)
    }

    private fun buildRequest(config: StreamSocketConfig): Request {
        val url =
            "${config.url}?" +
                "api_key=${config.apiKey.value}" +
                "&stream-auth-type=${config.authType}" +
                "&X-Stream-Client=${config.xStreamClient.value}"
        return Request.Builder()
            .url(url)
            .addHeader("Connection", "Upgrade")
            .addHeader("Upgrade", "websocket")
            .addHeader("X-Stream-Client", config.xStreamClient.value)
            .build()
    }
}
