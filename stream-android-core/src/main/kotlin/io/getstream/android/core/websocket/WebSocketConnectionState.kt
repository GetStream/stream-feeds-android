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
package io.getstream.android.core.websocket

import io.getstream.android.core.error.APIError
import io.getstream.android.core.error.isClientError
import io.getstream.android.core.error.isTokenInvalidErrorCode

/**
 * Represents the source that initiated a WebSocket disconnection. This helps determine the
 * appropriate reconnection behavior and error handling.
 */
public sealed interface DisconnectionSource {

    /**
     * Disconnection was initiated by the user or client application. This typically means no
     * automatic reconnection should be attempted.
     */
    public object UserInitiated : DisconnectionSource

    /**
     * Disconnection was initiated by the server.
     *
     * @param error The API error that caused the disconnection, if any. This can be used to
     *   determine if reconnection should be attempted.
     */
    public class ServerInitiated(public val error: APIError?) : DisconnectionSource

    /**
     * Disconnection was initiated by the system (e.g., network changes, app backgrounding). This
     * typically allows for automatic reconnection attempts.
     */
    public object SystemInitiated : DisconnectionSource

    /**
     * Disconnection occurred because no pong response was received from the server. This indicates
     * a potential network issue and typically allows for reconnection.
     */
    public object NoPongReceived : DisconnectionSource
}

/**
 * Represents the current state of a WebSocket connection. This sealed interface provides a
 * type-safe way to handle different connection states and their associated data.
 */
public sealed interface WebSocketConnectionState {
    /**
     * The initial state meaning that there was no attempt to connect yet. This is the starting
     * state before any connection attempts are made.
     */
    public object Initialized : WebSocketConnectionState

    /**
     * The WebSocket is currently attempting to establish a connection. This is a transitional state
     * between initialized/disconnected and connected.
     */
    public object Connecting : WebSocketConnectionState

    /**
     * The WebSocket connection is established and the client is authenticating. This is a
     * transitional state during the authentication process.
     */
    public object Authenticating : WebSocketConnectionState

    /**
     * The WebSocket is successfully connected and authenticated.
     *
     * @param connectionId The unique identifier for this connection session.
     *
     * TODO: Add the connect user details here
     */
    public data class Connected(public val connectionId: String) : WebSocketConnectionState

    /**
     * The WebSocket is in the process of disconnecting.
     *
     * @param source The source that initiated the disconnection process.
     */
    public data class Disconnecting(public val source: DisconnectionSource) :
        WebSocketConnectionState

    /**
     * The WebSocket is disconnected.
     *
     * @param source The source that caused the disconnection. This is used to determine
     *   reconnection behavior.
     */
    public data class Disconnected(public val source: DisconnectionSource) :
        WebSocketConnectionState

    /**
     * Checks if the WebSocket is currently connected and ready for communication.
     *
     * @return true if the connection state is [Connected], false otherwise.
     */
    public fun isConnected(): Boolean {
        return this is Connected
    }

    /**
     * Checks if the WebSocket connection is active (not disconnected). An active connection
     * includes connecting, authenticating, connected, and disconnecting states.
     *
     * @return true if the connection state is not [Disconnected], false otherwise.
     */
    public fun isActive(): Boolean {
        return this !is Disconnected
    }

    /**
     * Determines whether automatic reconnection should be enabled based on the current state.
     *
     * Reconnection is enabled for:
     * - Server-initiated disconnections (except for authentication errors and client errors)
     * - System-initiated disconnections (network changes, etc.)
     * - Disconnections due to missing pong responses
     *
     * Reconnection is disabled for:
     * - User-initiated disconnections
     * - Server errors with code 1000
     * - Token invalid errors
     * - Client errors (4xx status codes)
     *
     * @return true if automatic reconnection should be attempted, false otherwise.
     */
    public val isAutomaticReconnectionEnabled: Boolean
        get() =
            when (this) {
                is Disconnected ->
                    when (this.source) {
                        is DisconnectionSource.ServerInitiated ->
                            when {
                                // Do not reconnect on token invalid errors or client errors
                                this.source.error?.code == 1000 ->
                                    false // TODO: Define a constant for 1000
                                this.source.error?.isTokenInvalidErrorCode == true -> false
                                this.source.error?.isClientError == true -> false
                                // reconnect on other server initiated disconnections
                                else -> true
                            }
                        is DisconnectionSource.SystemInitiated -> true
                        is DisconnectionSource.NoPongReceived -> true
                        is DisconnectionSource.UserInitiated -> false
                    }
                else -> false
            }
}
