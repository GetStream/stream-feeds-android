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

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.ActivityReactionListState
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsFilterField
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An observable state object that manages the current state of an activity reaction list.
 *
 * This class provides reactive state management for a collection of activity reactions. It
 * automatically handles real-time updates when reactions are added or removed from the activity,
 * and maintains pagination state for loading additional reactions.
 *
 * @property query The query used to fetch activity reactions.
 */
internal class ActivityReactionListStateImpl(override val query: ActivityReactionsQuery) :
    ActivityReactionListMutableState {

    private val _reactions: MutableStateFlow<List<FeedsReactionData>> =
        MutableStateFlow(emptyList())

    internal var queryConfig:
        QueryConfiguration<ActivityReactionsFilterField, ActivityReactionsSort>? =
        null
        private set

    private var _pagination: PaginationData? = null

    private val reactionsSorting: List<ActivityReactionsSort>
        get() = query.sort ?: ActivityReactionsSort.Default

    override val reactions: StateFlow<List<FeedsReactionData>>
        get() = _reactions.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMoreActivityReactions(
        result: PaginationResult<FeedsReactionData>,
        queryConfig: QueryConfiguration<ActivityReactionsFilterField, ActivityReactionsSort>,
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new reactions with the existing ones (keeping the sort order)
        _reactions.value =
            _reactions.value.mergeSorted(result.models, FeedsReactionData::id, reactionsSorting)
    }

    override fun onReactionRemoved(reaction: FeedsReactionData) {
        _reactions.value = _reactions.value.filter { it.id != reaction.id }
    }
}

/**
 * A mutable state interface for managing the list of reactions to an activity.
 *
 * This interface combines the [ActivityReactionListState] for read access and
 * [ActivityReactionListStateUpdates] for write access, allowing for both querying and updating the
 * activity reaction list state.
 */
internal interface ActivityReactionListMutableState :
    ActivityReactionListState, ActivityReactionListStateUpdates

/** Interface for handling updates to the state of activity reactions in a list. */
internal interface ActivityReactionListStateUpdates {

    /**
     * Handles the loading of activity reactions.
     *
     * @param result The result of the pagination query containing the reactions.
     * @param queryConfig The configuration used for the query, including filters and sorting
     *   options.
     */
    fun onQueryMoreActivityReactions(
        result: PaginationResult<FeedsReactionData>,
        queryConfig: QueryConfiguration<ActivityReactionsFilterField, ActivityReactionsSort>,
    )

    /**
     * Handles the addition of a new reaction to the activity.
     *
     * @param reaction The reaction that was added.
     */
    fun onReactionRemoved(reaction: FeedsReactionData)
}
