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

import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.network.models.ActivityPinResponse
import io.getstream.feeds.android.network.models.PinActivityResponse

/**
 * Converts an [io.getstream.feeds.android.network.models.ActivityPinResponse] to an
 * [io.getstream.feeds.android.client.api.model.ActivityPinData] model.
 */
internal fun ActivityPinResponse.toModel(): ActivityPinData =
    ActivityPinData(
        activity = activity.toModel(),
        createdAt = createdAt,
        fid = FeedId(feed),
        updatedAt = createdAt,
        userId = user.id,
    )

/**
 * Converts a [io.getstream.feeds.android.network.models.PinActivityResponse] to an
 * [io.getstream.feeds.android.client.api.model.ActivityPinData] model.
 *
 * This conversion is used when pinning an activity to a feed, where the response contains the
 * pinned activity details.
 */
internal fun PinActivityResponse.toModel(): ActivityPinData =
    ActivityPinData(
        activity = activity.toModel(),
        createdAt = createdAt,
        fid = FeedId(feed),
        updatedAt = createdAt, // no updated_at
        userId = userId,
    )
