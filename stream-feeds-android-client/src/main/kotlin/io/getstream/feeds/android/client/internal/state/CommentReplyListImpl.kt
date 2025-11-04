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
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.CommentReplyList
import io.getstream.feeds.android.client.api.state.CommentReplyListState
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.state.event.handler.CommentReplyListEventHandler
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * A class representing a paginated list of replies for a specific comment.
 *
 * This class provides methods to fetch and manage replies to a comment, including pagination
 * support and real-time updates through WebSocket events. It maintains an observable state that
 * automatically updates when reply-related events are received.
 *
 * @property query The query configuration used to fetch replies.
 * @property currentUserId The ID of the current user.
 * @property commentsRepository The repository used to perform network requests for replies.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class CommentReplyListImpl(
    override val query: CommentRepliesQuery,
    private val currentUserId: String,
    private val commentsRepository: CommentsRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : CommentReplyList {

    private val _state: CommentReplyListStateImpl = CommentReplyListStateImpl(query, currentUserId)

    private val eventHandler = CommentReplyListEventHandler(_state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override val state: CommentReplyListState
        get() = _state

    override suspend fun get(): Result<List<ThreadedCommentData>> {
        return queryReplies(query)
    }

    override suspend fun queryMoreReplies(limit: Int?): Result<List<ThreadedCommentData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = query.copy(limit = limit ?: query.limit, next = next, previous = null)
        return queryReplies(nextQuery)
    }

    private suspend fun queryReplies(
        query: CommentRepliesQuery
    ): Result<List<ThreadedCommentData>> {
        return commentsRepository
            .getCommentReplies(query)
            .onSuccess { _state.onQueryMoreReplies(it) }
            .map { it.models }
    }
}
