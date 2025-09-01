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

import io.getstream.android.core.api.model.connection.StreamConnectionState
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * Handles re-watching feeds upon reconnection.
 *
 * Keeps track of feeds that are being watched and re-subscribes to them when the connection is
 * re-established.
 *
 * @property connectedEvents A [Flow] that emits events when the connection state changes to
 *   [StreamConnectionState.Connected].
 * @property feedsRepository The [FeedsRepository] used to rewatch feeds on connection.
 * @property scope The [CoroutineScope] in which to launch coroutines for re-watching feeds.
 */
internal class FeedWatchHandler(
    private val connectedEvents: Flow<StreamConnectionState>,
    private val feedsRepository: FeedsRepository,
    scope: CoroutineScope,
) {
    private val watched = mutableSetOf<FeedId>()

    init {
        scope.launch {
            connectedEvents.filterIsInstance<StreamConnectionState.Connected>().collect {
                rewatchAll()
            }
        }
    }

    @Synchronized
    fun onStartWatching(feedId: FeedId) {
        watched += feedId
    }

    @Synchronized
    fun onStopWatching(feedId: FeedId) {
        watched -= feedId
    }

    private suspend fun rewatchAll() {
        coroutineScope {
            synchronized(this@FeedWatchHandler) { watched.toSet() }
                .map { launch { feedsRepository.getOrCreateFeed(FeedQuery(it, watch = true)) } }
                .joinAll()
        }
    }
}
