package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.state.CommentList
import io.getstream.feeds.android.client.api.state.CommentListState
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.state.event.handler.CommentListEventHandler
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * A class representing a paginated list of comments for a specific query.
 *
 * This interface provides methods to fetch and manage comments, including pagination support
 * and real-time updates through WebSocket events. It maintains an observable state that
 * automatically updates when comment-related events are received.
 *
 * @property query The query used to fetch comments.
 * @property commentsRepository The repository used to perform network requests for comments.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 * updates.
 */
internal class CommentListImpl(
    override val query: CommentsQuery,
    private val commentsRepository: CommentsRepository,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>,
) : CommentList {

    init {
        subscriptionManager.subscribe(object : FeedsSocketListener {
            override fun onState(state: WebSocketConnectionState) {
                // Not relevant, rethink this
            }

            override fun onEvent(event: WSEvent) {
                eventHandler.handleEvent(event)
            }
        })
    }

    private val _state: CommentListStateImpl = CommentListStateImpl(query)

    private val eventHandler = CommentListEventHandler(_state)

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
        val nextQuery = CommentsQuery(
            filter = _state.filter,
            sort = _state.sort,
            limit = limit,
            next = next,
            previous = null,
        )
        return queryComments(nextQuery)
    }

    private suspend fun queryComments(query: CommentsQuery): Result<List<CommentData>> {
        return commentsRepository.queryComments(query)
            .onSuccess { result ->
                _state.onQueryMoreComments(result, query.filter, query.sort)
            }.map {
                it.models
            }
    }
}
