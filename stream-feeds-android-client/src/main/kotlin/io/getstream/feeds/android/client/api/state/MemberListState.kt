package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a member list.
 *
 * This class maintains maintains the current list of members, pagination information, and provides
 * real-time updates when members are added, removed, or modified.
 * It automatically handles WebSocket events to keep the member list synchronized.
 */
public interface MemberListState {

    /**
     * The original query configuration used to fetch members.
     *
     * This contains the feed ID, filters, and sorting options that were used to create the initial
     * member list.
     */
    public val query: MembersQuery

    /**
     * All the paginated members currently loaded.
     *
     * This array contains all members that have been fetched across multiple pagination requests.
     * The members are automatically sorted according to the current sorting configuration.
     */
    public val members: StateFlow<List<FeedMemberData>>

    /**
     * Last pagination information from the most recent request.
     *
     * Contains the `next` and `previous` cursor values that can be used  to fetch additional pages
     * of members.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more members to load.
     *
     * This is true if the `next` cursor in the pagination data is not null, meaning there are
     * additional pages of members available to fetch.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}