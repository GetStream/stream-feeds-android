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

package io.getstream.feeds.android.client.api.model

import java.util.Date

/**
 * Represents aggregated activity data in a feed.
 *
 * This class encapsulates a list of activities, their count, the group they belong to, the score of
 * the aggregation, and metadata such as creation and update timestamps.
 *
 * @property activities The list of activities included in this aggregation.
 * @property activityCount The total number of activities in this aggregation.
 * @property createdAt The date and time when this aggregation was created.
 * @property group The group identifier for this aggregation.
 * @property score The score associated with this aggregation.
 * @property updatedAt The date and time when this aggregation was last updated.
 * @property userCount The number of unique users involved in these activities.
 * @property userCountTruncated Indicates if the user count is truncated.
 */
public data class AggregatedActivityData(
    public val activities: List<ActivityData>,
    public val activityCount: Int,
    public val createdAt: Date,
    public val group: String,
    public val score: Float,
    public val updatedAt: Date,
    public val userCount: Int,
    public val userCountTruncated: Boolean,
) {

    /**
     * Returns a unique identifier for this aggregated activity data.
     *
     * The identifier is constructed from the first activity's ID, or a combination of the activity
     * count, user count, score, creation date, and group if no activities are present.
     */
    public val id: String
        get() =
            activities.firstOrNull()?.id ?: "$activityCount-$userCount-$score-$createdAt-($group)"
}
