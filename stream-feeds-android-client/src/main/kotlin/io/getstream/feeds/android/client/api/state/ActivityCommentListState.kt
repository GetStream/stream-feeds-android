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

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable object representing the current state of an activity's comment list.
 *
 * This class manages the state of comments for a specific activity, including the list of comments,
 * pagination information, and real-time updates from WebSocket events. It automatically handles
 * comment additions, updates, deletions, and reaction changes.
 *
 * ## Example:
 * ```kotlin
 * // Access the state from an ActivityCommentList
 * val commentList = feedsClient.activityCommentList(query)
 * val state = commentList.state
 *
 * // Observe comment changes
 * state.comments.collect { comments ->
 *     // Update UI with new comments
 * }
 *
 * // Check pagination status
 * if (state.canLoadMore) {
 *     // Load more comments
 * }
 *
 * // Access current comments
 * val currentComments = state.comments.value
 * ```
 *
 * ## Features
 * - **Observable State**: Uses flow properties for reactive UI updates
 * - **Real-time Updates**: Automatically receives WebSocket events for comment changes
 * - **Pagination Support**: Tracks pagination state for loading more comments
 * - **Change Handlers**: Internal handlers for processing WebSocket events
 */
public interface ActivityCommentListState {

    /** The query configuration used to fetch comments. */
    public val query: ActivityCommentsQuery

    /**
     * All the paginated comments for the activity.
     *
     * This property contains the current list of comments, including any threaded replies based on
     * the query configuration. The list is automatically updated when new comments are added,
     * existing comments are updated or deleted, or when reactions are added or removed.
     */
    public val comments: StateFlow<List<ThreadedCommentData>>

    /**
     * Last pagination information from the most recent API response.
     *
     * This property contains the pagination cursors and metadata from the last successful API
     * request. It's used to determine if more comments can be loaded and to construct subsequent
     * pagination requests.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more comments available to load.
     *
     * This computed property checks if a "next" cursor exists in the pagination data, indicating
     * that more comments can be fetched.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
