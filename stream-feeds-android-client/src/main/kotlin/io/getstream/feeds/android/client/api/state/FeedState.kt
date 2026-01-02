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

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.network.models.NotificationStatusResponse
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable object representing the current state of a feed.
 *
 * This class manages the state of a feed including activities, followers, members, and pagination
 * information. It automatically updates when WebSocket events are received and provides change
 * handlers for state modifications.
 */
public interface FeedState {

    /** The unique identifier for the feed. */
    public val fid: FeedId

    /** The query used to create the feed. */
    public val feedQuery: FeedQuery

    /** The list of activities in the feed, sorted by default sorting criteria. */
    public val activities: StateFlow<List<ActivityData>>

    /** The list of aggregated activities in the feed. */
    public val aggregatedActivities: StateFlow<List<AggregatedActivityData>>

    /** The feed data containing feed metadata and configuration. */
    public val feed: StateFlow<FeedData?>

    /** The list of followers for this feed. */
    public val followers: StateFlow<List<FollowData>>

    /** The list of feeds that this feed is following. */
    public val following: StateFlow<List<FollowData>>

    /** The list of pending follow requests for this feed. */
    public val followRequests: StateFlow<List<FollowData>>

    /** The list of members in this feed. */
    public val members: StateFlow<List<FeedMemberData>>

    /** The list of pinned activities and its pinning state. */
    public val pinnedActivities: StateFlow<List<ActivityPinData>>

    /** Returns information about the notification status (read / seen activities). */
    public val notificationStatus: StateFlow<NotificationStatusResponse?>

    /** Pagination information for activities queries. */
    public val activitiesPagination: PaginationData?

    /** Indicates whether there are more activities available to load. */
    public val canLoadMoreActivities: Boolean
        get() = activitiesPagination?.next != null
}
