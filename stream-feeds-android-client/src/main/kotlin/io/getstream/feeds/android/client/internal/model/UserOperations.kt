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

import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.network.models.UserResponse

/**
 * Converts a [io.getstream.feeds.android.network.models.UserResponse] to a
 * [io.getstream.feeds.android.client.api.model.UserData] model.
 */
internal fun UserResponse.toModel(): UserData =
    UserData(
        banned = banned,
        blockedUserIds = blockedUserIds,
        createdAt = createdAt,
        custom = custom,
        deactivatedAt = deactivatedAt,
        deletedAt = deletedAt,
        id = id,
        image = image,
        language = language,
        lastActive = lastActive,
        name = name,
        online = online,
        revokeTokensIssuedBefore = revokeTokensIssuedBefore,
        role = role,
        teams = teams,
        updatedAt = updatedAt,
    )
