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

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable state object that manages the activities list and handles real-time updates.
 *
 * [ActivityListState] provides a reactive interface for observing changes to the activities list,
 * including pagination state and real-time updates from WebSocket events. It automatically handles
 * sorting, merging, and updating activities as they change.
 *
 * Example usage:
 * ```kotlin
 * val activityList = client.activityList(query)
 * activityList.state.activities.collectLatest { activities ->
 *   // Update UI with new activities
 * }
 * ```
 */
public interface ActivityListState {

    /**
     * The query configuration used for fetching activities.
     *
     * This property contains the filtering, sorting, and pagination parameters that define how
     * activities should be fetched and displayed.
     */
    public val query: ActivitiesQuery

    /**
     * All the paginated activities in the current list.
     *
     * This property contains all activities that have been fetched and merged, maintaining the
     * proper sort order. It automatically updates when new activities are loaded, when real-time
     * events occur, or when activities are modified.
     */
    public val activities: StateFlow<List<ActivityData>>

    /**
     * Last pagination information from the most recent request.
     *
     * This property contains the pagination cursors and metadata from the last successful request.
     * It is used to determine if more activities can be loaded and to construct subsequent
     * pagination requests.
     */
    public val pagination: PaginationData?

    /**
     * Indicates whether there are more activities available to load.
     *
     * This computed property checks if a next page cursor exists in the pagination data. Use this
     * property to determine whether to show "Load More" buttons or implement infinite scrolling in
     * your UI.
     */
    public val canLoadMore: Boolean
        get() = pagination?.next != null
}
