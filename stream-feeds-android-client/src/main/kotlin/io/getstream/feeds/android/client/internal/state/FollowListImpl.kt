package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.FollowList
import io.getstream.feeds.android.client.api.state.FollowListState
import io.getstream.feeds.android.client.api.state.FollowsQuery
import io.getstream.feeds.android.client.api.state.toRequest
import io.getstream.feeds.android.client.internal.repository.FeedsRepository


/**
 * A class that manages a paginated list of feed members.
 *
 * [FollowList] provides functionality to query and paginate through follows.
 * It maintains the current state of the follow list and provides methods to load more follows
 * when available.
 *
 * @property query The query configuration used to fetch follows.
 * @property feedsRepository The repository used to perform network requests for follows.
 */
internal class FollowListImpl(
    override val query: FollowsQuery,
    private val feedsRepository: FeedsRepository,
) : FollowList {

    private val _state: FollowListStateImpl = FollowListStateImpl(query)

    override val state: FollowListState
        get() = _state

    override suspend fun get(): Result<List<FollowData>> {
        return queryFollows(query)
    }

    override suspend fun queryMoreFollows(limit: Int?): Result<List<FollowData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = FollowsQuery(
            filter = _state.queryConfig?.filter,
            sort = _state.queryConfig?.sort,
            limit = limit ?: query.limit,
            next = next,
            previous = null,
        )
        return queryFollows(nextQuery)
    }

    private suspend fun queryFollows(query: FollowsQuery): Result<List<FollowData>> {
        return feedsRepository.queryFollows(query.toRequest())
            .onSuccess {
                _state.onQueryMoreFollows(it, QueryConfiguration(query.filter, query.sort))
            }
            .map {
                it.models
            }
    }
}