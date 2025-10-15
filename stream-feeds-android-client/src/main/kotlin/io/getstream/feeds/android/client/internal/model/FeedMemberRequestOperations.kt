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
package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.FeedMemberRequestData
import io.getstream.feeds.android.network.models.FeedMemberRequest

/**
 * Converts a [io.getstream.feeds.android.network.models.FeedMemberRequest] to a
 * [io.getstream.feeds.android.client.api.model.FeedMemberRequestData] model.
 */
internal fun FeedMemberRequest.toModel(): FeedMemberRequestData =
    FeedMemberRequestData(userId = userId, invite = invite, role = role, custom = custom.orEmpty())

/**
 * Converts a [io.getstream.feeds.android.client.api.model.FeedMemberRequestData] to a
 * [io.getstream.feeds.android.network.models.FeedMemberRequest] model.
 */
internal fun FeedMemberRequestData.toRequest(): FeedMemberRequest =
    FeedMemberRequest(
        userId = userId,
        invite = invite,
        role = role,
        custom = custom.takeIf { it.isNotEmpty() },
    )
