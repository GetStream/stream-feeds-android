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
package io.getstream.feeds.android.client.internal.socket.common.listeners

import io.getstream.feeds.android.client.api.subscribe.StreamSubscriber
import okhttp3.Response
import okio.ByteString

/** Listener for the socket. */
internal interface StreamWebSocketListener : StreamSubscriber {
    /**
     * Called when the socket is created.
     *
     * @param response The response from the server.
     */
    fun onOpen(response: Response) {}

    /**
     * Called when a new message is received.
     *
     * @param bytes The bytes received.
     */
    fun onMessage(bytes: ByteString) {}

    /**
     * Called when a new message is received.
     *
     * @param text The text received.
     */
    fun onMessage(text: String) {}

    /**
     * Called when there is a failure in the socket.
     *
     * @param t The throwable.
     * @param response The response from the server.
     */
    fun onFailure(t: Throwable, response: Response?) {}

    /**
     * Called when the socket is closed.
     *
     * @param code The code.
     * @param reason The reason.
     */
    fun onClosed(code: Int, reason: String) {}

    /**
     * Called when the socket is closing.
     *
     * @param code The code.
     * @param reason The reason.
     */
    fun onClosing(code: Int, reason: String) {}
}
