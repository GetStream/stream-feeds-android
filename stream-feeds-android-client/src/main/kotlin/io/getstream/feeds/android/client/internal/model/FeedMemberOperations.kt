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

import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedMemberStatus
import io.getstream.feeds.android.network.models.FeedMemberResponse

/**
 * Converts a [io.getstream.feeds.android.network.models.FeedMemberResponse] to a
 * [io.getstream.feeds.android.client.api.model.FeedMemberData] model.
 */
internal fun FeedMemberResponse.toModel() =
    FeedMemberData(
        createdAt = createdAt,
        custom = custom,
        inviteAcceptedAt = inviteAcceptedAt,
        inviteRejectedAt = inviteRejectedAt,
        role = role,
        status = status.toModel(),
        updatedAt = updatedAt,
        user = user.toModel(),
    )

/**
 * Converts a [io.getstream.feeds.android.network.models.FeedMemberResponse.Status] to a
 * [io.getstream.feeds.android.client.api.model.FeedMemberStatus] model.
 */
internal fun FeedMemberResponse.Status.toModel(): FeedMemberStatus =
    when (this) {
        FeedMemberResponse.Status.Member -> FeedMemberStatus.Member
        FeedMemberResponse.Status.Pending -> FeedMemberStatus.Pending
        FeedMemberResponse.Status.Rejected -> FeedMemberStatus.Rejected
        is FeedMemberResponse.Status.Unknown -> FeedMemberStatus.Unknown(unknownValue)
    }
