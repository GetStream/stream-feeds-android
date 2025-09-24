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

import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.FeedListState
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQueryConfig
import io.getstream.feeds.android.client.api.state.query.FeedsSort
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An observable state object that manages the current state of a feed list.
 *
 * This class maintains the current list of feeds, pagination information, and provides real-time
 * updates when feeds are added, removed, or modified. It automatically handles WebSocket events to
 * keep the feed list synchronized.
 *
 * @property query The original query configuration used to fetch feeds.
 */
internal class FeedListStateImpl(override val query: FeedsQuery) : FeedListMutableState {

    private val _feeds: MutableStateFlow<List<FeedData>> = MutableStateFlow(emptyList())

    private var _pagination: PaginationData? = null

    internal var queryConfig: FeedsQueryConfig? = null
        private set

    private val feedsSorting: List<FeedsSort>
        get() = queryConfig?.sort ?: FeedsSort.Default

    override val feeds: StateFlow<List<FeedData>>
        get() = _feeds.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onFeedUpdated(feed: FeedData) {
        _feeds.update { current ->
            current.map {
                if (it.fid == feed.fid) {
                    feed
                } else {
                    it
                }
            }
        }
    }

    override fun onFeedRemoved(feedId: String) {
        _feeds.update { current -> current.filter { it.fid.rawValue != feedId } }
    }

    override fun onQueryMoreFeeds(
        result: PaginationResult<FeedData>,
        queryConfig: FeedsQueryConfig,
    ) {
        _pagination = result.pagination
        this.queryConfig = queryConfig
        // Merge the new feeds with the existing ones (keeping the sort order)
        _feeds.update { current ->
            current.mergeSorted(result.models, { it.fid.rawValue }, feedsSorting)
        }
    }
}

/**
 * A mutable state interface for managing the feed list state.
 *
 * This interface combines the [FeedListState] for read access and [FeedListStateUpdates] for
 * * write access, allowing for both querying and updating the feed list state.
 */
internal interface FeedListMutableState : FeedListState, FeedListStateUpdates

/**
 * An interface for handling updates to the feed list state.
 *
 * This interface defines methods to handle feed updates, pagination results, and query
 * configurations.
 */
internal interface FeedListStateUpdates {

    /** Handles updates to a specific feed. */
    fun onFeedUpdated(feed: FeedData)

    /** Handles the result of a query for more feeds. */
    fun onQueryMoreFeeds(result: PaginationResult<FeedData>, queryConfig: FeedsQueryConfig)

    /** Handles the removal of a feed by its ID. */
    fun onFeedRemoved(feedId: String)
}
