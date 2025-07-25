package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.FeedList
import io.getstream.feeds.android.client.api.state.FeedListState
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.state.event.handler.FeedListEventHandler
import io.getstream.feeds.android.core.generated.models.FeedUpdatedEvent
import io.getstream.feeds.android.core.generated.models.WSEvent
import kotlin.collections.emptyList

/**
 * Represents a list of feeds with a query and state.
 *
 * @property query The query used to fetch feeds.
 * @property feedsRepository The repository used to fetch feeds.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 * updates.
 */
internal class FeedListImpl(
    override val query: FeedsQuery,
    private val feedsRepository: FeedsRepository,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>
): FeedList {

    init {
        subscriptionManager.subscribe(object : FeedsSocketListener {
            override fun onState(state: WebSocketConnectionState) {
                // Not handled
            }

            override fun onEvent(event: WSEvent) {
                eventHandler.handleEvent(event)
            }
        })
    }

    private val _state: FeedListStateImpl = FeedListStateImpl(query)

    private val eventHandler = FeedListEventHandler(_state)

    override val state: FeedListState
        get() = _state

    override suspend fun get(): Result<List<FeedData>> {
        return queryFeeds(query)
    }

    override suspend fun queryMoreFeeds(limit: Int?): Result<List<FeedData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = FeedsQuery(
            filter = _state.queryConfig?.filter,
            sort = _state.queryConfig?.sort,
            limit = limit ?: query.limit,
            next = next,
            previous = null,
            watch = query.watch,
        )
        return queryFeeds(nextQuery)
    }

    private suspend fun queryFeeds(query: FeedsQuery): Result<List<FeedData>> {
        return feedsRepository.queryFeeds(query)
            .onSuccess {
                _state.onQueryMoreFeeds(it, QueryConfiguration(query.filter, query.sort))
            }.map {
                it.models
            }
    }
}