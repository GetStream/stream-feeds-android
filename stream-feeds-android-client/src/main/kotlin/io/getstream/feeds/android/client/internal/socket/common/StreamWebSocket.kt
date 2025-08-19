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
package io.getstream.feeds.android.client.internal.socket.common

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.subscribe.StreamSubscription
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.log.provideLogger
import io.getstream.feeds.android.client.internal.socket.common.factory.StreamSocketConfig
import io.getstream.feeds.android.client.internal.socket.common.factory.StreamWebSocketFactory
import io.getstream.feeds.android.client.internal.socket.common.listeners.StreamWebSocketListener
import io.getstream.log.TaggedLogger
import java.io.IOException
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString

internal interface StreamWebSocket<T : StreamWebSocketListener> : StreamSubscriptionManager<T> {

    companion object {
        /** Close code for the socket. */
        internal const val CLOSE_SOCKET_CODE = 1000

        /** Close reason for the socket. */
        internal const val CLOSE_SOCKET_REASON = "Closed by client"
    }

    fun open(config: StreamSocketConfig): Result<Unit>

    /** Close the socket. */
    fun close(): Result<Unit>

    /**
     * Send raw data to the socket.
     *
     * @param data The data to be sent.
     */
    fun send(data: ByteArray): Result<ByteArray>

    /**
     * Send text to the socket.
     *
     * @param text The text to be sent.
     */
    fun send(text: String): Result<String>
}

/** Implementation of the [StreamWebSocket] interface. */
internal open class StreamWebSocketImpl<T : StreamWebSocketListener>(
    private val logger: TaggedLogger = provideLogger(tag = "StreamWebSocket"),
    private val socketFactory: StreamWebSocketFactory,
    private val subscriptionManager: StreamSubscriptionManager<T>,
) : WebSocketListener(), StreamWebSocket<T> {

    private lateinit var socket: WebSocket

    override fun open(config: StreamSocketConfig): Result<Unit> = runSafely {
        socket =
            socketFactory
                .createSocket(config, this)
                .onFailure {
                    logger.e { "[open] SocketFactory failed to create socket. ${it.message}" }
                }
                .getOrThrow()
    }

    override fun close(): Result<Unit> = catchingWithSocket {
        logger.d { "[close] Closing socket" }
        socket.close(StreamWebSocket.CLOSE_SOCKET_CODE, StreamWebSocket.CLOSE_SOCKET_REASON)
    }

    override fun send(data: ByteArray): Result<ByteArray> = catchingWithSocket {
        logger.v { "[send] Sending data: $data" }
        if (data.isNotEmpty()) {
            val result = socket.send(data.toByteString(0, data.size))
            if (!result) {
                val message = "[send] socket.send() returned false"
                logger.e { message }
                throw IOException(message)
            }
            data
        } else {
            logger.e { "[send] Empty data!" }
            throw IllegalStateException("Empty raw data!")
        }
    }

    override fun send(text: String): Result<String> = catchingWithSocket {
        logger.v { "[send] Sending text: $text" }
        if (text.isNotEmpty()) {
            val result = socket.send(text)
            if (!result) {
                val message = "[send] socket.send() returned false"
                logger.e { message }
                throw IOException(message)
            }
            text
        } else {
            logger.e { "[send] Empty data!" }
            throw IllegalStateException("Empty raw data!")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        logger.d { "[onOpen] Socket is open" }
        forEach { it.onOpen(response) }
        super.onOpen(webSocket, response)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        logger.v { "[onMessage] Socket message: $bytes" }
        forEach { it.onMessage(bytes) }
        super.onMessage(webSocket, bytes)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        logger.v { "[onMessage] Socket message: $text" }
        forEach { it.onMessage(text) }
        super.onMessage(webSocket, text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        logger.e(t) { "[onFailure] Socket failure" }
        forEach { it.onFailure(t, response) }
        super.onFailure(webSocket, t, response)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        logger.d { "[onClosed] Socket closed. Code: $code, Reason: $reason" }
        forEach { it.onClosed(code, reason) }
        super.onClosed(webSocket, code, reason)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        logger.d { "[onClosing] Socket closing. Code: $code, Reason: $reason" }
        forEach { it.onClosing(code, reason) }
        super.onClosing(webSocket, code, reason)
    }

    override fun subscribe(listener: T): Result<StreamSubscription> =
        subscriptionManager.subscribe(listener)

    override fun clear(): Result<Unit> = subscriptionManager.clear()

    override fun forEach(block: (T) -> Unit): Result<Unit> = subscriptionManager.forEach(block)

    private inline fun <V> catchingWithSocket(block: (WebSocket) -> V) = runSafely {
        if (::socket.isInitialized) {
            block(socket)
        } else {
            val message =
                "[withSocket] The socket tried to use the internal web socket, but its not initialized. Call `open()` first."
            logger.e { message }
            throw IllegalStateException(message)
        }
    }
}
