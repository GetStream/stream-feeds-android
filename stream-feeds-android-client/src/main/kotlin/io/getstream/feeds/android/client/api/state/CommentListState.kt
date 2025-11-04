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

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the current state of a comment list.
 *
 * This interface maintains the current list of comments, pagination information, and provides
 * real-time updates. It automatically handles WebSocket events to keep the comment list
 * synchronized.
 *
 * ## Features
 * - **Observable State**: Uses flow properties for reactive UI updates
 * - **Real-time Updates**: Automatically receives WebSocket events for comment changes
 * - **Pagination Support**: Tracks pagination state for loading more comments
 * - **Change Handlers**: Internal handlers for processing WebSocket events
 */
public interface CommentListState {

    /** The query used to fetch the comments. */
    public val query: CommentsQuery

    /** All the paginated comments. */
    public val comments: StateFlow<List<CommentData>>

    /** Last pagination information. */
    public val pagination: PaginationData?

    /** Indicates whether there are more comments available to load. */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
