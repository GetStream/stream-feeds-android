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

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.network.models.AggregatedActivityResponse

/**
 * Converts an [io.getstream.feeds.android.network.models.AggregatedActivityResponse] to an
 * [io.getstream.feeds.android.client.api.model.AggregatedActivityData] model.
 */
internal fun AggregatedActivityResponse.toModel(): AggregatedActivityData {
    return AggregatedActivityData(
        activities = activities.map { it.toModel() },
        activityCount = activityCount,
        createdAt = createdAt,
        group = group,
        score = score,
        updatedAt = updatedAt,
        userCount = userCount,
        userCountTruncated = userCountTruncated,
    )
}
