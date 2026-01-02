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

import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery

/**
 * A class representing a paginated list of comments for a specific activity.
 *
 * This class provides methods to fetch and manage comments for an activity, including pagination
 * support and real-time updates through WebSocket events. It maintains an observable state that
 * automatically updates when comment-related events are received.
 *
 * ## Example:
 * ```kotlin
 * // Create a comment list for an activity
 * val commentList = feedsClient.activityCommentList(
 *      query = ActivityCommentsQuery(objectId = "activity-123", objectType = "activity")
 * )
 *
 * // Fetch initial comments
 * val comments = commentList.get()
 *
 * // Load more comments if available
 * if (commentList.state.canLoadMore) {
 *     val moreComments = commentList.queryMoreComments()
 * }
 *
 * // Observe state changes
 * commentList.state.comments.collect { comments ->
 *     println("Updated comments: ${comments.size}")
 * }
 * ```
 *
 * ## Features
 * - **Pagination**: Supports loading comments in pages with configurable limits
 * - **Real-time Updates**: Automatically receives WebSocket events for comment changes
 * - **Threaded Comments**: Supports nested comment replies
 * - **Reactions**: Tracks comment reactions and updates in real-time
 */
public interface ActivityCommentList {

    /** The query configuration used to fetch comments. */
    public val query: ActivityCommentsQuery

    /**
     * An observable object representing the current state of the comment list.
     *
     * This property provides access to the current comments, pagination information, and real-time
     * updates. The state automatically updates when WebSocket events are received for comment
     * additions, updates, deletions, and reactions.
     */
    public val state: ActivityCommentListState

    /**
     * Fetches the initial set of comments for the activity.
     *
     * This method retrieves the first page of comments based on the query configuration. The
     * results are automatically stored in the state and can be accessed through the
     * [state.comments] property.
     */
    public suspend fun get(): Result<List<ThreadedCommentData>>

    /**
     * Fetches the next page of comments based on the current pagination state.
     *
     * This method retrieves the next set of comments using the pagination cursor stored in the
     * state. It updates the state with the new comments and returns the results.
     *
     * @param limit The maximum number of comments to fetch in this request. If not specified, uses
     *   the limit from the original query.
     * @return A [Result] containing a list of [ThreadedCommentData] for the next page, or an error
     *   if the operation fails. Returns an empty list if there are no more comments to fetch.
     */
    public suspend fun queryMoreComments(limit: Int? = null): Result<List<ThreadedCommentData>>
}
