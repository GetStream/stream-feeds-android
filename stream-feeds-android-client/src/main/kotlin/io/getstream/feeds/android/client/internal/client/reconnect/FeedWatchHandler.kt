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

import io.getstream.android.core.api.model.connection.StreamConnectionState
import io.getstream.android.core.api.model.exceptions.StreamClientException
import io.getstream.android.core.api.model.retry.StreamRetryPolicy
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

/**
 * Handles re-watching feeds upon reconnection.
 *
 * Keeps track of feeds that are being watched and re-subscribes to them when the connection is
 * re-established. On a reconnection it leaves out the feeds that a refetch watches again, so those
 * are not requested twice.
 *
 * @property connectionState A [Flow] that emits events when the connection state changes to
 *   [StreamConnectionState.Connected].
 * @property feedsRepository The [FeedsRepository] used to rewatch feeds on connection.
 * @property scope The [CoroutineScope] in which to launch coroutines for re-watching feeds.
 * @property rewatchedByRefetch The feeds that the refetch on a reconnection starts watching again.
 */
internal class FeedWatchHandler(
    private val connectionState: Flow<StreamConnectionState>,
    private val feedsRepository: FeedsRepository,
    private val retryProcessor: StreamRetryProcessor,
    private val errorBus: MutableSharedFlow<StreamClientException>,
    private val scope: CoroutineScope,
    private val rewatchedByRefetch: () -> Set<FeedId> = ::emptySet,
) {
    private val watched = ConcurrentHashMap<FeedId, Unit>()

    init {
        scope.launch {
            var connectionId: String? = null
            connectionState.filterIsInstance<StreamConnectionState.Connected>().collect { connected
                ->
                // The refetch only runs on a reconnection, which is told apart the same way as in
                // ReconnectionRefetchHandler
                val reconnected = connectionId != null && connectionId != connected.connectionId
                connectionId = connected.connectionId
                rewatchAll(skipped = if (reconnected) rewatchedByRefetch() else emptySet())
            }
        }
    }

    fun onStartWatching(feedId: FeedId) {
        watched[feedId] = Unit
    }

    fun onStopWatching(feedId: FeedId) {
        watched -= feedId
    }

    fun isWatching(feedId: FeedId): Boolean = watched.containsKey(feedId)

    /**
     * Runs [fetch], a request that starts watching [id] again, with the retries and the error
     * reporting of a rewatch.
     */
    suspend fun rewatch(id: FeedId, fetch: suspend () -> GetOrCreateInfo): Result<GetOrCreateInfo> =
        retryProcessor
            .retry(retryPolicy) { fetch() }
            .onFailure { errorBus.emit(StreamFeedRewatchException(id)) }

    private suspend fun rewatchAll(skipped: Set<FeedId>) {
        coroutineScope {
            watched.keys.filterNot(skipped::contains).map { id -> async { rewatch(id) } }
        }
    }

    private suspend fun rewatch(id: FeedId): Result<GetOrCreateInfo> =
        rewatch(id) { feedsRepository.getOrCreateFeed(FeedQuery(id, watch = true)).getOrThrow() }

    companion object {
        private val retryPolicy = StreamRetryPolicy.exponential(maxRetries = 3)
    }
}

internal class StreamFeedRewatchException(val id: FeedId, cause: Throwable? = null) :
    StreamClientException("Failed to rewatch feed", cause)
