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

import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.BookmarkFolderList
import io.getstream.feeds.android.client.api.state.BookmarkFolderListState
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.state.event.handler.BookmarkFolderListEventHandler
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * A class that manages a paginated list of bookmark folders.
 *
 * [BookmarkFolderList] provides functionality to query and paginate through bookmark folders. It
 * maintains the current state of the bookmark folder list and provides methods to load more folders
 * when available.
 *
 * @property query The query used to fetch the bookmark folders.
 * @property bookmarksRepository The repository used to interact with the bookmark data source.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class BookmarkFolderListImpl(
    override val query: BookmarkFoldersQuery,
    private val bookmarksRepository: BookmarksRepository,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>,
) : BookmarkFolderList {

    init {
        subscriptionManager.subscribe(
            object : FeedsSocketListener {
                override fun onState(state: WebSocketConnectionState) {
                    // Not relevant, rethink this
                }

                override fun onEvent(event: WSEvent) {
                    eventHandler.handleEvent(event)
                }
            }
        )
    }

    private val _state: BookmarkFolderListStateImpl = BookmarkFolderListStateImpl(query)

    private val eventHandler = BookmarkFolderListEventHandler(_state)

    override val state: BookmarkFolderListState
        get() = _state

    override suspend fun get(): Result<List<BookmarkFolderData>> {
        return queryBookmarkFolders(query)
    }

    override suspend fun queryMoreBookmarkFolders(limit: Int?): Result<List<BookmarkFolderData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery =
            BookmarkFoldersQuery(
                filter = _state.queryConfig?.filter,
                sort = _state.queryConfig?.sort,
                limit = limit ?: query.limit,
                next = next,
                previous = null,
            )
        return queryBookmarkFolders(nextQuery)
    }

    private suspend fun queryBookmarkFolders(
        query: BookmarkFoldersQuery
    ): Result<List<BookmarkFolderData>> {
        return bookmarksRepository
            .queryBookmarkFolders(query)
            .onSuccess {
                _state.onQueryMoreBookmarkFolders(it, QueryConfiguration(query.filter, query.sort))
            }
            .map { it.models }
    }
}
