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
package io.getstream.feeds.android.client.internal.socket.common.reconnect

import io.getstream.android.core.lifecycle.StreamLifecycleObserver
import io.getstream.android.core.network.NetworkStateProvider
import io.getstream.android.core.websocket.DisconnectionSource
import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.internal.log.provideLogger
import io.getstream.feeds.android.client.internal.socket.FeedsSocket
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.network.models.WSEvent
import io.getstream.log.TaggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Handles the connection recovery logic for the [FeedsSocket].
 *
 * This class manages the reconnection attempts based on various policies and strategies. It listens
 * to lifecycle events and network state changes to determine when to reconnect or disconnect.
 *
 * @param scope The coroutine scope in which the reconnection logic will run.
 * @param socket The [FeedsSocket] instance to manage.
 * @param lifecycleObserver The [StreamLifecycleObserver] to observe app lifecycle events.
 * @param networkStateProvider The [NetworkStateProvider] to monitor network connectivity.
 * @param keepConnectionAliveInBackground Whether to keep the connection alive when the app is in
 *   the background.
 * @param reconnectStrategy The strategy used for determining reconnection delays.
 * @param automaticReconnectionPolicies List of policies that determine if reconnection should be
 *   attempted.
 */
internal class ConnectionRecoveryHandler(
    private val scope: CoroutineScope,
    private val socket: FeedsSocket,
    private val lifecycleObserver: StreamLifecycleObserver,
    private val networkStateProvider: NetworkStateProvider,
    private val keepConnectionAliveInBackground: Boolean,
    private val reconnectStrategy: RetryStrategy,
    private val automaticReconnectionPolicies: List<AutomaticReconnectionPolicy> =
        listOf(
            WebSocketAutomaticReconnectionPolicy(socket),
            BackgroundStateReconnectionPolicy(lifecycleObserver),
            InternetAvailabilityReconnectionPolicy(networkStateProvider),
        ),
    private val logger: TaggedLogger = provideLogger(tag = "ConnectionRecoveryHandler"),
) {

    private var reconnectionTimer: Job? = null

    private val lifecycleListener =
        object : StreamLifecycleObserver.LifecycleListener {
            override suspend fun onResume() {
                logger.d { "[onResume] App -> Resumed" }
                reconnectIfNeeded()
            }

            override suspend fun onStop() {
                logger.d { "[onStop] App -> Stopped" }
                if (!canDisconnect) {
                    logger.d {
                        "[onStop] Socket is not connecting/connected (${socket.connectionState})"
                    }
                    return
                }
                if (keepConnectionAliveInBackground) {
                    logger.d { "[onStop] Keeping connection alive in background" }
                    return
                }
                logger.d { "[onStop] Disconnecting (if needed)" }
                disconnectIfNeeded()
            }
        }

    private val networkStateListener =
        object : NetworkStateProvider.NetworkStateListener {
            override suspend fun onConnected() {
                logger.d { "[onConnected] Network available" }
                reconnectIfNeeded()
            }

            override suspend fun onDisconnected() {
                logger.d {
                    "[odDisconnected] Network not available. Ignoring this event as the " +
                        "connection interruption would close the socket."
                }
            }
        }

    private val stateListener =
        object : FeedsSocketListener {
            override fun onState(state: WebSocketConnectionState) {
                when (state) {
                    is WebSocketConnectionState.Connecting -> {
                        cancelReconnectionTimer()
                    }
                    is WebSocketConnectionState.Connected -> {
                        reconnectStrategy.resetConsecutiveFailures()
                    }
                    is WebSocketConnectionState.Disconnected -> {
                        scheduleReconnectionTimerIfNeeded()
                    }
                    else -> Unit
                }
            }

            override fun onEvent(event: WSEvent) {
                // Not relevant for connection recovery
            }
        }

    private val canReconnect: Boolean
        get() = automaticReconnectionPolicies.all(AutomaticReconnectionPolicy::shouldReconnect)

    private val canDisconnect: Boolean
        get() =
            when (socket.connectionState) {
                is WebSocketConnectionState.Connecting,
                is WebSocketConnectionState.Authenticating,
                is WebSocketConnectionState.Connected -> true
                else -> false
            }

    /**
     * Starts the connection recovery handler by subscribing to the necessary listeners. This method
     * should be called when the socket is initialized.
     */
    internal suspend fun start() {
        socket.subscribe(stateListener)
        lifecycleObserver.subscribe(lifecycleListener)
        networkStateProvider.subscribe(networkStateListener)
    }

    /**
     * Stops the connection recovery handler by unsubscribing from the listeners. This method should
     * be called when the socket is no longer needed.
     */
    internal suspend fun stop() {
        lifecycleObserver.unsubscribe(lifecycleListener)
        networkStateProvider.unsubscribe(networkStateListener)
    }

    private fun scheduleReconnectionTimerIfNeeded() {
        if (!canReconnect) {
            logger.w { "[scheduleReconnectionTimerIfNeeded] Preconditions for reconnection failed" }
            return
        }
        scheduleReconnectionTimer()
    }

    private fun scheduleReconnectionTimer() {
        val delay = reconnectStrategy.nextRetryDelay()
        // Increment the number of consecutive failures to ensure that the next reconnection attempt
        // will have a longer delay
        reconnectStrategy.incrementConsecutiveFailures()
        logger.d { "[scheduleReconnectionTime] Scheduling reconnection in $delay millis" }
        cancelReconnectionTimer()
        scope.launch {
            delay(delay)
            if (!isActive) return@launch
            reconnectIfNeeded()
        }
    }

    private fun cancelReconnectionTimer() {
        reconnectionTimer?.let {
            it.cancel()
            logger.d { "[cancelReconnectionTimer] Reconnection timer cancelled" }
        }
        reconnectionTimer = null
    }

    private suspend fun reconnectIfNeeded() {
        if (!canReconnect) {
            logger.w { "[reconnectIfNeeded] Preconditions for reconnect failed" }
            return
        }
        // TODO: Connect with the last used connection configuration
        logger.d { "[reconnectIfNeeded] Reconnecting..." }
        socket.connect()
    }

    private fun disconnectIfNeeded() {
        if (!canDisconnect) {
            logger.w { "[disconnectIfNeeded] Preconditions for disconnect failed" }
            return
        }
        logger.d { "[disconnectIfNeeded] Disconnecting..." }
        socket.disconnect(DisconnectionSource.SystemInitiated)
    }
}
