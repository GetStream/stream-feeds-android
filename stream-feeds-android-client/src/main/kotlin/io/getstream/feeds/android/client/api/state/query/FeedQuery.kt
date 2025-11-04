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

package io.getstream.feeds.android.client.api.state.query

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedInputData

/**
 * A query configuration for retrieving and managing feed data from Stream feeds.
 *
 * @param fid The unique identifier for the feed.
 * @param activityFilter Filter criteria for activities in the feed. See [ActivitiesFilterField] for
 *   available filter fields and their supported operators.
 * @param activityLimit Maximum number of activities to retrieve.
 * @param activityNext Pagination cursor for fetching the next page of activities.
 * @param activityPrevious Pagination cursor for fetching the previous page of activities.
 * @param activitySelectorOptions Custom options for activity selection and processing.
 * @param data Additional data to associate with the feed.
 * @param externalRanking Additional data used for ranking activities in the feed.
 * @param followerLimit Maximum number of followers to retrieve.
 * @param followingLimit Maximum number of following users to retrieve.
 * @param interestWeights Weights for different interests to influence activity ranking.
 * @param memberLimit Maximum number of feed members to retrieve.
 * @param view Overwrite the default ranking or aggregation logic for this feed (for example: good
 *   for split testing).
 * @param watch If true, subscribes to web-socket events for this feed.
 */
public data class FeedQuery(
    public val fid: FeedId,
    public val activityFilter: ActivitiesFilter? = null,
    public val activityLimit: Int? = null,
    public val activityNext: String? = null,
    public val activityPrevious: String? = null,
    public val activitySelectorOptions: Map<String, Any>? = null,
    public val data: FeedInputData? = null,
    public val externalRanking: Map<String, Any>? = null,
    public val followerLimit: Int? = null,
    public val followingLimit: Int? = null,
    public val interestWeights: Map<String, Float>? = null,
    public val memberLimit: Int? = null,
    public val view: String? = null,
    public val watch: Boolean = true,
) {

    /**
     * Creates a new [FeedQuery] instance with the specified parameters.
     *
     * @param group The feed group (e.g., "user", "timeline", "notification").
     * @param id The unique identifier within the group.
     * @param activityFilter Filter criteria for activities in the feed.
     * @param activityLimit Maximum number of activities to retrieve.
     * @param activityNext Pagination cursor for fetching the next page of activities.
     * @param activityPrevious Pagination cursor for fetching the previous page of activities.
     * @param activitySelectorOptions Custom options for activity selection and processing.
     * @param data Additional data to associate with the feed.
     * @param externalRanking Additional data used for ranking activities in the feed.
     * @param followerLimit Maximum number of followers to retrieve.
     * @param followingLimit Maximum number of following users to retrieve.
     * @param interestWeights Weights for different interests to influence activity ranking.
     * @param memberLimit Maximum number of feed members to retrieve.
     * @param view Overwrite the default ranking or aggregation logic for this feed (for example:
     *   good for split testing).
     * @param watch If true, subscribes to web-socket events for this feed.
     */
    public constructor(
        group: String,
        id: String,
        activityFilter: ActivitiesFilter? = null,
        activityLimit: Int? = null,
        activityNext: String? = null,
        activityPrevious: String? = null,
        activitySelectorOptions: Map<String, Any>? = null,
        data: FeedInputData? = null,
        externalRanking: Map<String, Any>? = null,
        followerLimit: Int? = null,
        followingLimit: Int? = null,
        interestWeights: Map<String, Float>? = null,
        memberLimit: Int? = null,
        view: String? = null,
        watch: Boolean = true,
    ) : this(
        fid = FeedId(group = group, id = id),
        activityFilter = activityFilter,
        activityLimit = activityLimit,
        activityNext = activityNext,
        activityPrevious = activityPrevious,
        activitySelectorOptions = activitySelectorOptions,
        data = data,
        externalRanking = externalRanking,
        followerLimit = followerLimit,
        followingLimit = followingLimit,
        interestWeights = interestWeights,
        memberLimit = memberLimit,
        view = view,
        watch = watch,
    )
}
