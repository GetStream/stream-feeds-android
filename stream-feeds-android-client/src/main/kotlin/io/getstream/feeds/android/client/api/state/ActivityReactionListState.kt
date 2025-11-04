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

package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of an activity reaction list.
 *
 * This class provides reactive state management for a collection of activity reactions. It
 * automatically handles real-time updates when reactions are added or removed from the activity,
 * and maintains pagination state for loading additional reactions.
 *
 * ## Example:
 * ```kotlin
 * // Access the state from an ActivityReactionList
 * val reactionList = feedsClient.activityReactionList(query)
 * val state = reactionList.state
 *
 * // Observe reaction changes
 * state.reactions.collect { reactions ->
 *    // Update UI with new reactions
 * }
 *
 * // Check pagination status
 * if (state.canLoadMore) {
 *    // Load more reactions
 * }
 *
 * // Access current reactions
 * val currentReactions = state.reactions.value
 * ```
 */
public interface ActivityReactionListState {

    /**
     * The query configuration used to fetch activity reactions.
     *
     * This contains the activity ID, filters, sorting options, and pagination parameters that
     * define how reactions should be fetched and displayed.
     */
    public val query: ActivityReactionsQuery

    /**
     * The current collection of activity reactions.
     *
     * This array contains all the reactions that have been loaded for the activity. The reactions
     * are automatically sorted according to the query configuration. Changes to this array are
     * published and can be observed for UI updates.
     */
    public val reactions: StateFlow<List<FeedsReactionData>>

    /**
     * The pagination information from the last query.
     *
     * This contains the pagination cursors (`next` and `previous`) that can be used to fetch
     * additional pages of reactions. The pagination data is automatically updated when new pages
     * are loaded.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more reactions available to load.
     *
     * This property returns `true` if there are additional pages of reactions available to fetch.
     * You can use this to determine whether to show a "Load More" button or implement infinite
     * scrolling.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
