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
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.PollList
import io.getstream.feeds.android.client.api.state.PollListState
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.state.event.handler.PollListEventHandler
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * Implementation of [PollList] that manages the state of a list of polls.
 *
 * This class handles querying polls, managing pagination, and responding to WebSocket events
 * related to poll updates. It maintains an internal state that reflects the current list of polls
 * and their pagination status.
 *
 * @property query The query configuration used to fetch polls.
 * @property pollsRepository The repository used to perform network requests for polls.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 * @property _state The internal state of the poll list.
 */
internal class PollListImpl(
    override val query: PollsQuery,
    private val currentUserId: String,
    private val pollsRepository: PollsRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : PollList {
    private val _state: PollListStateImpl = PollListStateImpl(currentUserId = currentUserId, query)

    override val state: PollListState
        get() = _state

    private val eventHandler = PollListEventHandler(query.filter, _state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override suspend fun get(): Result<List<PollData>> {
        return queryPolls(query)
    }

    override suspend fun queryMorePolls(limit: Int?): Result<List<PollData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // No more polls to load, return emptyList()
            return Result.success(emptyList())
        }
        val nextQuery =
            PollsQuery(
                filter = _state.queryConfig?.filter,
                sort = _state.queryConfig?.sort,
                limit = limit,
                next = next,
                previous = null,
            )
        return queryPolls(nextQuery)
    }

    private suspend fun queryPolls(query: PollsQuery): Result<List<PollData>> {
        return pollsRepository
            .queryPolls(query)
            .onSuccess { _state.onQueryMorePolls(it, QueryConfiguration(query.filter, query.sort)) }
            .map { it.models }
    }
}
