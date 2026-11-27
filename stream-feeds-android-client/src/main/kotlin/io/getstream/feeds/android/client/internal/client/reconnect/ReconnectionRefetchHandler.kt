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

package io.getstream.feeds.android.client.internal.client.reconnect

import io.getstream.android.core.api.log.StreamLogger
import io.getstream.android.core.api.model.connection.StreamConnectionState
import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.FeedId
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

/** A state object that can load its data again, replacing what it has loaded. */
internal fun interface Refetchable {
    /**
     * The feed that [refetch] starts watching again, if any. Such a feed needs no separate request
     * to watch it again after a reconnection.
     */
    val rewatchedFeed: FeedId?
        get() = null

    /**
     * Loads the data again and replaces the loaded data with it. Does nothing if no data is loaded.
     * Throws if a request fails.
     */
    suspend fun refetch()
}

/**
 * Refetches the loaded data of the registered state objects when the connection is established
 * again.
 *
 * Events sent while the connection is down are never delivered, so any loaded state can be out of
 * date after a reconnection.
 *
 * @property connectionState A [Flow] of the connection state.
 * @property refetchables The registered state objects. They are held weakly.
 * @property logger The logger for refetch failures.
 * @property scope The [CoroutineScope] in which to refetch.
 * @param enabled Whether to refetch on reconnection.
 */
internal class ReconnectionRefetchHandler(
    private val connectionState: Flow<StreamConnectionState>,
    private val refetchables: StreamSubscriptionManager<Refetchable>,
    private val logger: StreamLogger,
    private val scope: CoroutineScope,
    private val enabled: Boolean,
) {
    init {
        if (enabled) {
            scope.launch { refetchOnReconnection() }
        }
    }

    /** Registers [refetchable] for a refetch on every reconnection, for as long as it is in use. */
    fun register(refetchable: Refetchable) {
        refetchables.subscribe(refetchable).onFailure {
            logger.e(it) { "[register] Failed to register for a refetch on reconnection" }
        }
    }

    /** The feeds that the refetch on the next reconnection starts watching again. */
    fun rewatchedFeeds(): Set<FeedId> = buildSet {
        if (enabled) {
            refetchables.forEach { refetchable -> refetchable.rewatchedFeed?.let(::add) }
        }
    }

    private suspend fun refetchOnReconnection() {
        var connectionId: String? = null
        var refetchJob: Job? = null
        connectionState.filterIsInstance<StreamConnectionState.Connected>().collect { connected ->
            // Every connection has its own id, so the first one is not a reconnection
            val reconnected = connectionId != null && connectionId != connected.connectionId
            connectionId = connected.connectionId
            if (reconnected) {
                // A newer reconnection cancels a refetch that is still running, so older data is
                // not applied last
                refetchJob?.cancel()
                refetchJob = scope.launch { refetchAll() }
            }
        }
    }

    private fun CoroutineScope.refetchAll() {
        val targets = buildList { refetchables.forEach(::add) }
        logger.d { "[refetchAll] Refetching ${targets.size} state objects after reconnection" }
        targets.forEach { target -> launch { refetch(target) } }
    }

    private suspend fun refetch(target: Refetchable) {
        try {
            target.refetch()
        } catch (e: CancellationException) {
            throw e
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.e(e) { "[refetch] Refetching data after reconnection failed" }
        }
    }
}
