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

import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.FollowListState
import io.getstream.feeds.android.client.api.state.query.FollowsFilterField
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.api.state.query.FollowsSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a follow list.
 *
 * This class maintains the current list of follows, pagination information, and provides real-time
 * updates when follows are added, removed, or modified. It automatically handles WebSocket events
 * to keep the follow list synchronized.
 */
internal class FollowListStateImpl(override val query: FollowsQuery) : FollowListMutableState {

    private val _follows: MutableStateFlow<List<FollowData>> = MutableStateFlow(emptyList())

    internal var queryConfig: QueryConfiguration<FollowsFilterField, FollowsSort>? = null
        private set

    private var _pagination: PaginationData? = null

    private val followsSorting: List<FollowsSort>
        get() = queryConfig?.sort ?: FollowsSort.Default

    override val follows: StateFlow<List<FollowData>>
        get() = _follows.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreFollows(
        result: PaginationResult<FollowData>,
        queryConfig: QueryConfiguration<FollowsFilterField, FollowsSort>,
    ) {
        _pagination = result.pagination
        this.queryConfig = queryConfig
        // Merge the new follows with the existing ones (keeping the sort order)
        _follows.update { current ->
            current.mergeSorted(result.models, FollowData::id, followsSorting)
        }
    }

    override fun onFollowUpdated(follow: FollowData) {
        _follows.update { current ->
            current.map {
                if (it.id == follow.id) {
                    follow
                } else {
                    it
                }
            }
        }
    }

    override fun onFollowRemoved(follow: FollowData) {
        _follows.update { current -> current.filter { it.id != follow.id } }
    }
}

/**
 * A mutable state interface for managing follow list state updates.
 *
 * This interface combines the [FollowListState] for read access and [FollowListStateUpdates] for
 * write access, allowing for both querying and updating the follow list state.
 */
internal interface FollowListMutableState : FollowListState, FollowListStateUpdates

/** An interface for handling updates to the follow list state. */
internal interface FollowListStateUpdates {

    /** Handles the result of a query for more follows. */
    fun onQueryMoreFollows(
        result: PaginationResult<FollowData>,
        queryConfig: QueryConfiguration<FollowsFilterField, FollowsSort>,
    )

    /** Handles the update of a follow data. */
    fun onFollowUpdated(follow: FollowData)

    /** Handles the removal of a follow. */
    fun onFollowRemoved(follow: FollowData)
}
