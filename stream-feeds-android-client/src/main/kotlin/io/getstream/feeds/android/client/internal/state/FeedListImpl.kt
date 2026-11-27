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

package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.state.FeedList
import io.getstream.feeds.android.client.api.state.FeedListState
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.internal.client.reconnect.PaginatedRefetchable
import io.getstream.feeds.android.client.internal.client.reconnect.ReconnectionRefetchHandler
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.repository.FeedOwnValuesRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.cacheEnriched
import io.getstream.feeds.android.client.internal.state.event.handler.FeedListEventHandler
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * Represents a list of feeds with a query and state.
 *
 * @property query The query used to fetch feeds.
 * @property feedsRepository The repository used to fetch feeds.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class FeedListImpl(
    override val query: FeedsQuery,
    private val feedsRepository: FeedsRepository,
    private val feedOwnValuesRepository: FeedOwnValuesRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
    reconnectionRefetchHandler: ReconnectionRefetchHandler,
) : FeedList, PaginatedRefetchable<FeedData> {

    private val _state: FeedListStateImpl = FeedListStateImpl(query)

    private val eventHandler = FeedListEventHandler(query.filter, _state)

    init {
        subscriptionManager.subscribe(eventHandler)
        reconnectionRefetchHandler.register(this)
    }

    override val state: FeedListState
        get() = _state

    override suspend fun get(): Result<List<FeedData>> {
        return queryFeeds(query, replace = true)
    }

    override suspend fun queryMoreFeeds(limit: Int?): Result<List<FeedData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = query.copy(limit = limit ?: query.limit, next = next, previous = null)
        return queryFeeds(nextQuery)
    }

    override fun loadedCount(): Int? = _state.pagination?.let { _state.feeds.value.size }

    override fun idOf(model: FeedData): Any = model.fid

    override suspend fun fetchPage(next: String?): PaginationResult<FeedData> {
        val page = if (next == null) query else query.copy(next = next, previous = null)
        return feedsRepository.queryFeeds(page).getOrThrow().also {
            feedOwnValuesRepository.cacheEnriched(it.models)
        }
    }

    override fun onRefetched(result: PaginationResult<FeedData>) {
        _state.onQueryFeeds(result, QueryConfiguration(query.filter, query.sort), replace = true)
    }

    private suspend fun queryFeeds(
        query: FeedsQuery,
        replace: Boolean = false,
    ): Result<List<FeedData>> {
        return feedsRepository
            .queryFeeds(query)
            .onSuccess {
                _state.onQueryFeeds(it, QueryConfiguration(query.filter, query.sort), replace)
                feedOwnValuesRepository.cacheEnriched(it.models)
            }
            .map { it.models }
    }
}
