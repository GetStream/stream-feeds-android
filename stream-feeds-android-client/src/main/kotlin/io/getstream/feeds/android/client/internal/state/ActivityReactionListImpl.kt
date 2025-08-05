package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.ActivityReactionList
import io.getstream.feeds.android.client.api.state.ActivityReactionListState
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.state.event.handler.ActivityReactionListEventHandler
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * A list of activity reactions that provides pagination, filtering, and real-time updates.
 *
 * This class manages a collection of reactions for a specific activity. It provides methods
 * to fetch reactions with pagination support and automatically handles real-time updates
 * when reactions are added or removed from the activity.
 *
 * @property query The query configuration used to fetch activity reactions.
 * @property activitiesRepository The repository used to perform network operations for activity
 * reactions.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 * updates.
 */
internal class ActivityReactionListImpl(
    override val query: ActivityReactionsQuery,
    private val activitiesRepository: ActivitiesRepository,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>,
) : ActivityReactionList {

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

    private val _state = ActivityReactionListStateImpl(query)

    private val eventHandler = ActivityReactionListEventHandler(query.activityId,  _state)

    override val state: ActivityReactionListState
        get() = _state

    override suspend fun get(): Result<List<FeedsReactionData>> {
        return queryActivityReactions(query)
    }

    override suspend fun queryMoreReactions(limit: Int?): Result<List<FeedsReactionData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = ActivityReactionsQuery(
            activityId = query.activityId,
            filter = _state.queryConfig?.filter,
            sort = _state.queryConfig?.sort,
            limit = limit,
            next = next,
            previous = null,
        )
        return queryActivityReactions(nextQuery)
    }

    private suspend fun queryActivityReactions(
        query: ActivityReactionsQuery,
    ): Result<List<FeedsReactionData>> {
        return activitiesRepository.queryActivityReactions(query.activityId, query.toRequest())
            .onSuccess {
                _state.onQueryMoreActivityReactions(
                    result = it,
                    queryConfig = QueryConfiguration(query.filter, query.sort),
                )
            }
            .map {
                it.models
            }
    }
}