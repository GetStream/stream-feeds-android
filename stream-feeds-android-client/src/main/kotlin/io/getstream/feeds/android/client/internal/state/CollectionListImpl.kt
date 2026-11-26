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
import io.getstream.feeds.android.client.api.model.CollectionData
import io.getstream.feeds.android.client.api.state.CollectionList
import io.getstream.feeds.android.client.api.state.CollectionListState
import io.getstream.feeds.android.client.api.state.query.CollectionsQuery
import io.getstream.feeds.android.client.internal.repository.CollectionsRepository
import io.getstream.feeds.android.client.internal.state.event.handler.CollectionListEventHandler
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * A paginated list of collections matching a query.
 *
 * @property query The query used to fetch the collections.
 * @property collectionsRepository The repository used to fetch the collections.
 * @property subscriptionManager The manager for state update subscriptions.
 */
internal class CollectionListImpl(
    override val query: CollectionsQuery,
    private val collectionsRepository: CollectionsRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : CollectionList {

    private val _state: CollectionListStateImpl = CollectionListStateImpl(query)

    private val eventHandler = CollectionListEventHandler(query.filter, _state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override val state: CollectionListState
        get() = _state

    override suspend fun get(): Result<List<CollectionData>> {
        return queryCollections(query, replace = true)
    }

    override suspend fun queryMoreCollections(limit: Int?): Result<List<CollectionData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = query.copy(limit = limit ?: query.limit, next = next, previous = null)
        return queryCollections(nextQuery)
    }

    private suspend fun queryCollections(
        query: CollectionsQuery,
        replace: Boolean = false,
    ): Result<List<CollectionData>> {
        return collectionsRepository
            .queryCollections(query)
            .onSuccess { _state.onQueryCollections(it, replace) }
            .map { it.models }
    }
}
