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
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.state.BookmarkList
import io.getstream.feeds.android.client.api.state.BookmarkListState
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.state.event.handler.BookmarkListEventHandler
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * A class that manages a paginated list of bookmarks.
 *
 * [BookmarkList] provides functionality to query and paginate through bookmarks. It maintains the
 * current state of the bookmark list and provides methods to load more bookmarks when available.
 *
 * @property query The query configuration used to fetch the bookmarks.
 * @property bookmarksRepository The repository used to perform network requests for bookmarks.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class BookmarkListImpl(
    override val query: BookmarksQuery,
    private val bookmarksRepository: BookmarksRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : BookmarkList {

    private val _state: BookmarkListStateImpl = BookmarkListStateImpl(query)

    private val eventHandler = BookmarkListEventHandler(query.filter, _state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override val state: BookmarkListState
        get() = _state

    override suspend fun get(): Result<List<BookmarkData>> {
        return queryBookmarks(query)
    }

    override suspend fun queryMoreBookmarks(limit: Int?): Result<List<BookmarkData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery =
            BookmarksQuery(
                filter = _state.queryConfig?.filter,
                sort = _state.queryConfig?.sort,
                limit = limit ?: query.limit,
                next = next,
                previous = null,
            )
        return queryBookmarks(nextQuery)
    }

    private suspend fun queryBookmarks(query: BookmarksQuery): Result<List<BookmarkData>> {
        return bookmarksRepository
            .queryBookmarks(query)
            .onSuccess {
                _state.onQueryMoreBookmarks(it, QueryConfiguration(query.filter, query.sort))
            }
            .map { it.models }
    }
}
