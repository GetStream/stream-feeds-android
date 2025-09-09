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

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.state.PollListState
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.api.state.query.PollsQueryConfig
import io.getstream.feeds.android.client.api.state.query.PollsSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a poll list.
 *
 * This class provides a way to fetch and manage a collection of polls with support for filtering,
 * sorting, and pagination. It maintains an observable state that can be used in UI components.
 *
 * @property query The query used to fetch the polls.
 */
internal class PollListStateImpl(override val query: PollsQuery) : PollListMutableState {

    private val _polls: MutableStateFlow<List<PollData>> = MutableStateFlow(emptyList())

    internal var queryConfig: PollsQueryConfig? = null
        private set

    private var _pagination: PaginationData? = null

    private val pollsSorting: List<PollsSort>
        get() = query.sort ?: PollsSort.Default

    override val polls: StateFlow<List<PollData>>
        get() = _polls.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onQueryMorePolls(
        result: PaginationResult<PollData>,
        queryConfig: PollsQueryConfig,
    ) {
        _pagination = result.pagination
        // Update the query configuration for future queries
        this.queryConfig = queryConfig
        // Merge the new polls with the existing ones (keeping the sort order)
        _polls.update { current -> current.mergeSorted(result.models, PollData::id, pollsSorting) }
    }

    override fun onPollUpdated(poll: PollData) {
        _polls.update { current ->
            current.map {
                if (it.id == poll.id) {
                    // Update the existing poll with the new data
                    poll
                } else {
                    it
                }
            }
        }
    }
}

/**
 * Mutable state object that manages the current state of a poll list.
 *
 * This interface extends [PollListState] and [PollListStateUpdates] interfaces, allowing for both
 * state observation and updates.
 */
internal interface PollListMutableState : PollListState, PollListStateUpdates

/** Interface for handling updates to the poll list state. */
internal interface PollListStateUpdates {

    /**
     * Handles when a new list of polls is fetched.
     *
     * @param result The result containing the list of polls.
     * @param queryConfig The configuration used for the query, including sorting options.
     */
    fun onQueryMorePolls(result: PaginationResult<PollData>, queryConfig: PollsQueryConfig)

    /**
     * Called when a poll is updated.
     *
     * @param poll The updated poll data.
     */
    fun onPollUpdated(poll: PollData)
}
