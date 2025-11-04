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
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery

/**
 * A class representing a paginated list of reactions for a specific comment.
 *
 * This class provides methods to fetch and manage reactions for a comment, including pagination
 * support and real-time updates through WebSocket events. It maintains an observable state that
 * automatically updates when reaction-related events are received.
 *
 * ## Example:
 * ```kotlin
 * // Create a reaction list for a comment
 * val query = CommentReactionsQuery(commentId = "comment-123")
 * val reactionList = feedsClient.commentReactionList(query)
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
 *     Log.d(TAG, "Updated reactions: ${reactions.size}")
 * }
 * ```
 *
 * ## Features
 * - **Pagination**: Supports loading reactions in pages with configurable limits
 * - **Real-time Updates**: Automatically receives WebSocket events for reaction changes
 * - **Filtering**: Supports filtering by reaction type, user ID, and creation date
 * - **Sorting**: Configurable sorting options for reaction ordering
 * - **Observable State**: Provides reactive state management for UI updates
 */
public interface CommentReactionList {

    /**
     * The query configuration used to fetch comment reactions.
     *
     * This contains the comment ID, filters, sorting options, and pagination parameters that define
     * how reactions should be fetched and displayed.
     */
    public val query: CommentReactionsQuery

    /**
     * An observable object representing the current state of the comment reaction list.
     *
     * This property provides access to the current reactions, pagination information, and real-time
     * updates. The state automatically updates when WebSocket events are received for reaction
     * additions, updates, and deletions.
     */
    public val state: CommentReactionListState

    /**
     * Fetches the initial set of reactions for the comment.
     *
     * This method retrieves the first page of reactions based on the query configuration. The
     * results are automatically stored in the state and can be accessed through the
     * [state.reactions] property.
     *
     * @return A [Result] containing a list of [FeedsReactionData] if the fetch is successful, or an
     *   error if the fetch fails.
     */
    public suspend fun get(): Result<List<FeedsReactionData>>

    /**
     * Loads the next page of reactions if more are available.
     *
     * This method fetches additional reactions using the pagination information from the previous
     * request. If no more reactions are available, an empty array is returned.
     *
     * @param limit Optional limit for the number of reactions to fetch. If not specified, the
     *   default API limit will be used.
     * @return A [Result] containing a list of [FeedsReactionData] if the fetch is successful, or an
     *   error if the fetch fails. Returns an empty list if there are no more reactions to fetch.
     */
    public suspend fun queryMoreReactions(limit: Int? = null): Result<List<FeedsReactionData>>
}
