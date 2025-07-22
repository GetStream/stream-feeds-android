package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.MemberList
import io.getstream.feeds.android.client.api.state.MemberListState
import io.getstream.feeds.android.client.api.state.MembersQuery
import io.getstream.feeds.android.client.api.state.toRequest
import io.getstream.feeds.android.client.internal.repository.FeedsRepository

/**
 * A class that manages a paginated list of feed members.
 *
 * [MemberList] provides functionality to query and paginate through members of a specific feed.
 * It maintains the current state of the member list and provides methods to load more members
 * when available.
 *
 * @property query The query configuration used to fetch members.
 * @property feedsRepository The repository used to perform network requests for members.
 */
internal class MemberListImpl(
    override val query: MembersQuery,
    private val feedsRepository: FeedsRepository,
    // TODO: Observe events
) : MemberList {

    private val _state: MemberListStateImpl = MemberListStateImpl(query)

    override val state: MemberListState
        get() = _state

    override suspend fun get(): Result<List<FeedMemberData>> {
        return queryMembers(query)
    }

    override suspend fun queryMoreMembers(limit: Int?): Result<List<FeedMemberData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery = MembersQuery(
            fid = query.fid,
            filter = _state.queryConfig?.filter,
            sort = _state.queryConfig?.sort,
            limit = limit ?: query.limit,
            next = next,
            previous = null,
        )
        return queryMembers(nextQuery)
    }

    /**
     * Internal property to access the mutable state of the member list.
     */
    internal val mutableState: MemberListMutableState
        get() = _state

    private suspend fun queryMembers(query: MembersQuery): Result<List<FeedMemberData>> {
        return feedsRepository.queryFeedMembers(
            feedGroupId = query.fid.group,
            feedId = query.fid.id,
            request = query.toRequest(),
        ).onSuccess {
            _state.onQueryMoreMembers(it, QueryConfiguration(query.filter, query.sort))
        }.map {
            it.models
        }
    }
}