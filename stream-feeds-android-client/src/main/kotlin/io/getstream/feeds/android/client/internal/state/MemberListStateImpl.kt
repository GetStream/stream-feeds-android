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

import io.getstream.android.core.api.sort.CompositeComparator
import io.getstream.android.core.api.sort.Sort
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.MemberListState
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.api.state.query.MembersSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.state.query.MembersQueryConfig
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a member list.
 *
 * This class maintains maintains the current list of members, pagination information, and provides
 * real-time updates when members are added, removed, or modified. It automatically handles
 * WebSocket events to keep the member list synchronized.
 */
internal class MemberListStateImpl(override val query: MembersQuery) : MemberListMutableState {

    private val _members: MutableStateFlow<List<FeedMemberData>> = MutableStateFlow(emptyList())

    internal var queryConfig: MembersQueryConfig? = null
        private set

    private var _pagination: PaginationData? = null

    private val membersSorting: List<Sort<FeedMemberData>>
        get() = queryConfig?.sort ?: MembersSort.Default

    override val members: StateFlow<List<FeedMemberData>>
        get() = _members.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreMembers(
        result: PaginationResult<FeedMemberData>,
        queryConfig: MembersQueryConfig,
    ) {
        _pagination = result.pagination
        this.queryConfig = queryConfig
        // Merge the new members with the existing ones (keeping the sort order)
        _members.update { current ->
            current.mergeSorted(result.models, FeedMemberData::id, membersSorting)
        }
    }

    override fun onMemberAdded(member: FeedMemberData) {
        _members.update { current ->
            current.upsertSorted(member, FeedMemberData::id, membersSorting)
        }
    }

    override fun onMemberRemoved(memberId: String) {
        _members.update { current -> current.filter { it.id != memberId } }
    }

    override fun onMemberUpdated(member: FeedMemberData) {
        _members.update { current ->
            current.upsertSorted(member, FeedMemberData::id, membersSorting)
        }
    }

    override fun onMembersUpdated(updates: ModelUpdates<FeedMemberData>) {
        // Create a map for efficient lookups of updated members
        val updatesMap = updates.updated.associateBy(FeedMemberData::id)

        _members.update { current ->
            current
                .mapNotNullTo(mutableListOf()) { member ->
                    // Apply updates and filter out removed members in a single pass
                    if (member.id in updates.removedIds) {
                        null
                    } else {
                        updatesMap[member.id] ?: member
                    }
                }
                .apply {
                    addAll(updates.added)
                    sortWith(CompositeComparator(membersSorting))
                }
        }
    }

    override fun clear() {
        _members.update { emptyList() }
    }
}

/**
 * A mutable state interface for managing member list state updates.
 *
 * This interface combines the [MemberListState] for read access and [MemberListStateUpdates] for
 * write access, allowing for both querying and updating the member list state.
 */
internal interface MemberListMutableState : MemberListState, MemberListStateUpdates

/**
 * An interface for handling updates to the member list state.
 *
 * This interface defines methods for updating the member list when new members are queried, members
 * are removed, or members are updated. It also provides a method to clear the state.
 */
internal interface MemberListStateUpdates {

    /** Handles the result of a query for more members. */
    fun onQueryMoreMembers(
        result: PaginationResult<FeedMemberData>,
        queryConfig: MembersQueryConfig,
    )

    /** Handles the addition of a new member. */
    fun onMemberAdded(member: FeedMemberData)

    /** Handles the removal of a member by their ID. */
    fun onMemberRemoved(memberId: String)

    /** Handles the update of a member's data. */
    fun onMemberUpdated(member: FeedMemberData)

    /** Handles updates to multiple members. */
    fun onMembersUpdated(updates: ModelUpdates<FeedMemberData>)

    /** Clears the current member list state. */
    fun clear()
}
