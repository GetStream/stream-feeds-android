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

package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.state.FeedList
import io.getstream.feeds.android.client.api.state.FeedListState
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.repository.FeedOwnValuesRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.cache
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
) : FeedList {

    private val _state: FeedListStateImpl = FeedListStateImpl(query)

    private val eventHandler = FeedListEventHandler(query.filter, _state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override val state: FeedListState
        get() = _state

    override suspend fun get(): Result<List<FeedData>> {
        return queryFeeds(query)
    }

    override suspend fun queryMoreFeeds(limit: Int?): Result<List<FeedData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery =
            FeedsQuery(
                filter = _state.queryConfig?.filter,
                sort = _state.queryConfig?.sort,
                limit = limit ?: query.limit,
                next = next,
                previous = null,
                watch = query.watch,
            )
        return queryFeeds(nextQuery)
    }

    private suspend fun queryFeeds(query: FeedsQuery): Result<List<FeedData>> {
        return feedsRepository
            .queryFeeds(query)
            .onSuccess {
                _state.onQueryMoreFeeds(it, QueryConfiguration(query.filter, query.sort))
                feedOwnValuesRepository.cache(it.models)
            }
            .map { it.models }
    }
}
