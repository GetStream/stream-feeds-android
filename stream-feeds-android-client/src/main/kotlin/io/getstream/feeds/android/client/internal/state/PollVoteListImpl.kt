package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.websocket.WebSocketConnectionState
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.PollVoteList
import io.getstream.feeds.android.client.api.state.PollVoteListState
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.state.event.handler.PollVoteListEventHandler
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * An implementation of [PollVoteList] that manages the state and queries for poll votes.
 *
 * This class provides methods to fetch the initial list of poll votes and to query more votes
 * using pagination. It maintains an internal state that tracks the current list of votes,
 * pagination information, and query configuration.
 *
 * @property query The query used to fetch poll votes.
 * @property repository The repository used to perform the actual data fetching.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time
 * updates.
 * @property _state The internal state object that holds the current state of the poll vote list.
 */
internal class PollVoteListImpl(
    override val query: PollVotesQuery,
    private val repository: PollsRepository,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>,
    private val _state: PollVoteListStateImpl = PollVoteListStateImpl(query)
) : PollVoteList {

    init {
        subscriptionManager.subscribe(object : FeedsSocketListener {
            override fun onState(state: WebSocketConnectionState) {
                // Not relevant for this implementation
            }

            override fun onEvent(event: WSEvent) {
                eventHandler.handleEvent(event)
            }
        })
    }

    private val eventHandler = PollVoteListEventHandler(query.pollId, _state)

    override val state: PollVoteListState
        get() = _state

    override suspend fun get(): Result<List<PollVoteData>> {
        return queryPollVotes(query)
    }

    override suspend fun queryMorePollVotes(limit: Int?): Result<List<PollVoteData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // No more pages to fetch, return an empty list
            return Result.success(emptyList())
        }
        val nextQuery = PollVotesQuery(
            pollId = query.pollId,
            userId = query.userId,
            filter = _state.queryConfig?.filter,
            sort = _state.queryConfig?.sort,
            limit = limit,
            next = next,
            previous = null,
        )
        return queryPollVotes(nextQuery)
    }

    private suspend fun queryPollVotes(query: PollVotesQuery): Result<List<PollVoteData>> {
        return repository.queryPollVotes(query)
            .onSuccess {
                _state.onQueryMorePollVotes(it, QueryConfiguration(query.filter, query.sort))
            }
            .map {
                it.models
            }
    }
}