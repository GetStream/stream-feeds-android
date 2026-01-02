/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.ActivityReactionListState
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.upsertReactionSorted
import io.getstream.feeds.android.client.internal.state.query.ActivityReactionsQueryConfig
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    internal var queryConfig: ActivityReactionsQueryConfig? = null
        private set

    private var _pagination: PaginationData? = null

    private val reactionsSorting: CompositeComparator<FeedsReactionData> =
        CompositeComparator(query.sort ?: ActivityReactionsSort.Default)

    override val reactions: StateFlow<List<FeedsReactionData>>
        get() = _reactions.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onActivityRemoved() {
        _reactions.update { emptyList() }
    }

    override fun onQueryMoreActivityReactions(
        result: PaginationResult<FeedsReactionData>,
        queryConfig: ActivityReactionsQueryConfig,
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new reactions with the existing ones (keeping the sort order)
        _reactions.update { current ->
            current.mergeSorted(result.models, FeedsReactionData::id, reactionsSorting)
        }
    }

    override fun onReactionUpserted(reaction: FeedsReactionData, enforceUnique: Boolean) {
        _reactions.update { current ->
            current.upsertReactionSorted(reaction, enforceUnique, reactionsSorting)
        }
    }

    override fun onReactionRemoved(reaction: FeedsReactionData) {
        _reactions.update { current -> current.filter { it.id != reaction.id } }
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

    /** Handles the deletion of the parent activity. */
    fun onActivityRemoved()

    /**
     * Handles the loading of activity reactions.
     *
     * @param result The result of the pagination query containing the reactions.
     * @param queryConfig The configuration used for the query, including filters and sorting
     *   options.
     */
    fun onQueryMoreActivityReactions(
        result: PaginationResult<FeedsReactionData>,
        queryConfig: ActivityReactionsQueryConfig,
    )

    /**
     * Handles the addition or update of a reaction to the activity.
     *
     * @param reaction The reaction that was added.
     * @param enforceUnique Whether to replace existing reactions by the same user.
     */
    fun onReactionUpserted(reaction: FeedsReactionData, enforceUnique: Boolean)

    /**
     * Handles the removal of a reaction from the activity.
     *
     * @param reaction The reaction that was added.
     */
    fun onReactionRemoved(reaction: FeedsReactionData)
}
