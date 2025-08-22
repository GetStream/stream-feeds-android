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
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.ActivityCommentList
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.state.event.handler.ActivityCommentListEventHandler
import io.getstream.feeds.android.network.models.WSEvent

/**
 * A paginated list of activities that supports real-time updates and filtering.
 *
 * `ActivityList` provides a convenient way to fetch, paginate, and observe activities with
 * automatic real-time updates via WebSocket events. It manages the state of activities and provides
 * methods for loading more activities as needed.
 *
 * @property query The query configuration used for fetching comments.
 * @property currentUserId The ID of the current user.
 * @property commentsRepository The repository used to fetch comments.
 */
internal class ActivityCommentListImpl(
    override val query: ActivityCommentsQuery,
    private val currentUserId: String,
    private val commentsRepository: CommentsRepository,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>,
) : ActivityCommentList {

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

    private val _state: ActivityCommentListStateImpl =
        ActivityCommentListStateImpl(query, currentUserId)

    private val eventHandler =
        ActivityCommentListEventHandler(
            objectId = query.objectId,
            objectType = query.objectType,
            state = _state,
        )

    override val state: ActivityCommentListState
        get() = _state

    override suspend fun get(): Result<List<ThreadedCommentData>> {
        return queryComments(query)
    }

    override suspend fun queryMoreComments(limit: Int?): Result<List<ThreadedCommentData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = query.copy(limit = limit, next = next, previous = null)
        return queryComments(nextQuery)
    }

    /** Internal property to access the mutable state of the comment list. */
    internal val mutableState: ActivityCommentListMutableState
        get() = _state

    private suspend fun queryComments(
        query: ActivityCommentsQuery
    ): Result<List<ThreadedCommentData>> {
        return commentsRepository
            .getComments(query)
            .onSuccess { _state.onQueryMoreComments(it) }
            .map { it.models }
    }
}
