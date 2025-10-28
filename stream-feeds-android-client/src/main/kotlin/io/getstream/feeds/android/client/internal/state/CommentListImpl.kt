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
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.state.CommentList
import io.getstream.feeds.android.client.api.state.CommentListState
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.state.event.handler.CommentListEventHandler
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * A class representing a paginated list of comments for a specific query.
 *
 * This interface provides methods to fetch and manage comments, including pagination support and
 * real-time updates through WebSocket events. It maintains an observable state that automatically
 * updates when comment-related events are received.
 *
 * @property query The query used to fetch comments.
 * @property commentsRepository The repository used to perform network requests for comments.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class CommentListImpl(
    override val query: CommentsQuery,
    private val commentsRepository: CommentsRepository,
    currentUserId: String,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : CommentList {

    private val _state: CommentListStateImpl = CommentListStateImpl(query, currentUserId)

    private val eventHandler = CommentListEventHandler(query.filter, _state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override val state: CommentListState
        get() = _state

    override suspend fun get(): Result<List<CommentData>> {
        return queryComments(query)
    }

    override suspend fun queryMoreComments(limit: Int?): Result<List<CommentData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = query.copy(limit = limit, next = next, previous = null)
        return queryComments(nextQuery)
    }

    private suspend fun queryComments(query: CommentsQuery): Result<List<CommentData>> {
        return commentsRepository.queryComments(query).onSuccess(_state::onQueryMoreComments).map {
            it.models
        }
    }
}
