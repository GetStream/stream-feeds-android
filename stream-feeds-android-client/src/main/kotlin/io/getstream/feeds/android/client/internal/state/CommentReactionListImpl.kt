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
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.state.CommentReactionList
import io.getstream.feeds.android.client.api.state.CommentReactionListState
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.internal.model.QueryConfiguration
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.state.event.handler.CommentReactionListEventHandler
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * A class representing a paginated list of reactions for a specific comment.
 *
 * This class provides methods to fetch and manage reactions for a comment, including pagination
 * support and real-time updates through WebSocket events. It maintains an observable state that
 * automatically updates when reaction-related events are received.
 *
 * @property query The query configuration used to fetch comment reactions.
 * @property commentsRepository The repository used to interact with comment reactions.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 *   updates.
 */
internal class CommentReactionListImpl(
    override val query: CommentReactionsQuery,
    private val commentsRepository: CommentsRepository,
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener>,
) : CommentReactionList {

    private val _state: CommentReactionListStateImpl = CommentReactionListStateImpl(query)

    private val eventHandler = CommentReactionListEventHandler(query.commentId, _state)

    init {
        subscriptionManager.subscribe(eventHandler)
    }

    override val state: CommentReactionListState
        get() = _state

    override suspend fun get(): Result<List<FeedsReactionData>> {
        return queryCommentReactions(query)
    }

    override suspend fun queryMoreReactions(limit: Int?): Result<List<FeedsReactionData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = query.copy(limit = limit, next = next, previous = null)
        return queryCommentReactions(nextQuery)
    }

    private suspend fun queryCommentReactions(
        query: CommentReactionsQuery
    ): Result<List<FeedsReactionData>> {
        return commentsRepository
            .queryCommentReactions(query.commentId, query)
            .onSuccess { result ->
                _state.onQueryMoreReactions(result, QueryConfiguration(query.filter, query.sort))
            }
            .map { it.models }
    }
}
