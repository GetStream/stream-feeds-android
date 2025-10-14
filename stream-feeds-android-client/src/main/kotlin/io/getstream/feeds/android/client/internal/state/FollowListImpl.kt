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
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.state.FollowList
import io.getstream.feeds.android.client.api.state.FollowListState
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.state.event.handler.FollowListEventHandler
import io.getstream.feeds.android.client.internal.state.query.toRequest
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * A class that manages a paginated list of follows.
 *
 * [FollowList] provides functionality to query and paginate through follows. It maintains the
 * current state of the follow list and provides methods to load more follows when available.
 *
 * @property query The query configuration used to fetch follows.
 * @property feedsRepository The repository used to perform network requests for follows.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class FollowListImpl(
    override val query: FollowsQuery,
    private val feedsRepository: FeedsRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : FollowList {

    private val _state: FollowListStateImpl = FollowListStateImpl(query)

    private val eventHandler = FollowListEventHandler(_state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override val state: FollowListState
        get() = _state

    override suspend fun get(): Result<List<FollowData>> {
        return queryFollows(query)
    }

    override suspend fun queryMoreFollows(limit: Int?): Result<List<FollowData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery =
            FollowsQuery(
                filter = _state.queryConfig?.filter,
                sort = _state.queryConfig?.sort,
                limit = limit ?: query.limit,
                next = next,
                previous = null,
            )
        return queryFollows(nextQuery)
    }

    private suspend fun queryFollows(query: FollowsQuery): Result<List<FollowData>> {
        return feedsRepository
            .queryFollows(query.toRequest())
            .onSuccess {
                _state.onQueryMoreFollows(it, QueryConfiguration(query.filter, query.sort))
            }
            .map { it.models }
    }
}
