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
package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedSuggestionData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.state.query.ActivitiesQueryConfig
import io.getstream.feeds.android.network.models.AcceptFollowRequest
import io.getstream.feeds.android.network.models.FollowRequest
import io.getstream.feeds.android.network.models.NotificationStatusResponse
import io.getstream.feeds.android.network.models.QueryFeedMembersRequest
import io.getstream.feeds.android.network.models.QueryFollowsRequest
import io.getstream.feeds.android.network.models.RejectFollowRequest
import io.getstream.feeds.android.network.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.network.models.UpdateFeedRequest

/**
 * Represents the repository for managing feeds. Performs requests and transforms API models to
 * domain models.
 */
internal interface FeedsRepository {

    // BEGIN: Creating or Getting the state of the feed

    suspend fun getOrCreateFeed(query: FeedQuery): Result<GetOrCreateInfo>

    suspend fun stopWatching(groupId: String, feedId: String): Result<Unit>

    // END: Creating or Getting the state of the feed

    // BEGIN: Manging the feed

    suspend fun deleteFeed(feedGroupId: String, feedId: String, hardDelete: Boolean): Result<Unit>

    suspend fun updateFeed(
        feedGroupId: String,
        feedId: String,
        request: UpdateFeedRequest,
    ): Result<FeedData>

    // END: Manging the feed

    // BEGIN: Feed lists

    suspend fun queryFeeds(query: FeedsQuery): Result<PaginationResult<FeedData>>

    // END: Feed lists

    // BEGIN: Follows

    suspend fun queryFollowSuggestions(
        feedGroupId: String,
        limit: Int?,
    ): Result<List<FeedSuggestionData>>

    suspend fun queryFollows(request: QueryFollowsRequest): Result<PaginationResult<FollowData>>

    suspend fun follow(request: FollowRequest): Result<FollowData>

    suspend fun unfollow(source: FeedId, target: FeedId): Result<FollowData>

    suspend fun acceptFollow(request: AcceptFollowRequest): Result<FollowData>

    suspend fun rejectFollow(request: RejectFollowRequest): Result<FollowData>

    // END: Follows

    // BEGIN: Members

    suspend fun updateFeedMembers(
        feedGroupId: String,
        feedId: String,
        request: UpdateFeedMembersRequest,
    ): Result<ModelUpdates<FeedMemberData>>

    suspend fun acceptFeedMember(feedGroupId: String, feedId: String): Result<FeedMemberData>

    suspend fun rejectFeedMember(feedGroupId: String, feedId: String): Result<FeedMemberData>

    suspend fun queryFeedMembers(
        feedGroupId: String,
        feedId: String,
        request: QueryFeedMembersRequest,
    ): Result<PaginationResult<FeedMemberData>>

    // END: Members
}

/**
 * Data class representing the information returned when getting or creating a feed.
 *
 * @property activities A paginated result of activities associated with the feed.
 * @property activitiesQueryConfig The configuration used to query activities.
 * @property feed The feed data associated with the feed.
 * @property followers A list of followers for the feed.
 * @property following A list of feeds that this feed is following.
 * @property followRequests A list of follow requests for the feed.
 * @property members A paginated result of members in the feed.
 * @property pinnedActivities A list of activities that are pinned in the feed.
 * @property aggregatedActivities A list of aggregated activities in the feed.
 * @property notificationStatus The notification status for the feed, if available.
 */
internal data class GetOrCreateInfo(
    val activities: PaginationResult<ActivityData>,
    val activitiesQueryConfig: ActivitiesQueryConfig,
    val feed: FeedData,
    val followers: List<FollowData>,
    val following: List<FollowData>,
    val followRequests: List<FollowData>,
    val members: PaginationResult<FeedMemberData>,
    val pinnedActivities: List<ActivityPinData>,
    val aggregatedActivities: List<AggregatedActivityData>,
    val notificationStatus: NotificationStatusResponse?,
)
