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

package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery

/**
 * A list of activity reactions that provides pagination, filtering, and real-time updates.
 *
 * This class manages a collection of reactions for a specific activity. It provides methods to
 * fetch reactions with pagination support and automatically handles real-time updates when
 * reactions are added or removed from the activity.
 *
 * ## Example:
 * ```kotlin
 * val query = ActivityReactionsQuery(activityId = "activity-123")
 * val reactionList = feedsClient.activityReactionList(query)
 *
 * // Fetch initial reactions
 * val reactions = reactionList.get()
 *
 * // Load more reactions if available
 * if (reactionList.state.canLoadMore) {
 *     val moreReactions = reactionList.queryMoreReactions()
 * }
 *
 * // Observe state changes
 * reactionList.state.reactions.collect { reactions ->
 *     println("Updated reactions: ${reactions.size}")
 * }
 * ```
 */
public interface ActivityReactionList {

    /**
     * The query configuration used to fetch activity reactions.
     *
     * This contains the activity ID, filters, sorting options, and pagination parameters that
     * define how reactions should be fetched and displayed.
     */
    public val query: ActivityReactionsQuery

    /**
     * An observable object representing the current state of the activity reaction list.
     *
     * This state object contains the current reactions, pagination information, and loading state.
     * You can observe changes to this state to update your UI when reactions are added, removed, or
     * when new pages are loaded.
     */
    public val state: ActivityReactionListState

    /**
     * Fetches the initial page of activity reactions.
     *
     * This method retrieves the first page of reactions for the activity based on the query
     * configuration. Results are automatically stored in the state and can be accessed through
     * [state.reactions].
     *
     * @return A [Result] containing a list of [FeedsReactionData] if successful, or an error if the
     *   request fails.
     */
    public suspend fun get(): Result<List<FeedsReactionData>>

    /**
     * Fetches the next page of activity reactions.
     *
     * This method retrieves additional reactions if more are available. The method uses the
     * pagination cursor from the previous request to fetch the next page of results.
     *
     * @return A [Result] containing a list of [FeedsReactionData] if successful, or an error if the
     *   request fails. Returns an empty list if there are no more reactions to fetch.
     */
    public suspend fun queryMoreReactions(limit: Int? = null): Result<List<FeedsReactionData>>
}
