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
package io.getstream.feeds.android.client.internal.client.reconnect

import io.getstream.android.core.api.filter.`in`
import io.getstream.android.core.api.model.StreamRetryPolicy
import io.getstream.android.core.api.model.connection.StreamConnectionState
import io.getstream.android.core.api.model.exceptions.StreamClientException
import io.getstream.android.core.api.processing.StreamRetryProcessor
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.query.FeedsFilterField
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

/**
 * Handles re-watching feeds upon reconnection.
 *
 * Keeps track of feeds that are being watched and re-subscribes to them when the connection is
 * re-established.
 *
 * @property connectionState A [Flow] that emits events when the connection state changes to
 *   [StreamConnectionState.Connected].
 * @property feedsRepository The [FeedsRepository] used to rewatch feeds on connection.
 * @property scope The [CoroutineScope] in which to launch coroutines for re-watching feeds.
 */
internal class FeedWatchHandler(
    private val connectionState: Flow<StreamConnectionState>,
    private val feedsRepository: FeedsRepository,
    private val retryProcessor: StreamRetryProcessor,
    private val errorBus: MutableSharedFlow<StreamClientException>,
    private val scope: CoroutineScope,
) {
    private val watched = ConcurrentHashMap<FeedId, Unit>()

    init {
        scope.launch {
            connectionState.filterIsInstance<StreamConnectionState.Connected>().collect {
                rewatchAll()
            }
        }
    }

    fun onStartWatching(feedId: FeedId) {
        watched[feedId] = Unit
    }

    fun onStopWatching(feedId: FeedId) {
        watched -= feedId
    }

    private suspend fun rewatchAll() {
        val watchedCopy = watched.keys.toSet()

        if (watchedCopy.isNotEmpty()) {
            val toRewatch = watchedCopy.map(FeedId::rawValue)

            retryProcessor
                .retry(retryPolicy) {
                    feedsRepository
                        .queryFeeds(FeedsQuery(FeedsFilterField.feed.`in`(toRewatch)))
                        .getOrThrow()
                }
                .onFailure { errorBus.emit(StreamFeedRewatchException(watchedCopy, it)) }
        }
    }

    companion object {
        private val retryPolicy = StreamRetryPolicy.exponential(maxRetries = 3)
    }
}

internal class StreamFeedRewatchException(val ids: Set<FeedId>, cause: Throwable? = null) :
    StreamClientException("Failed to rewatch feeds", cause)
