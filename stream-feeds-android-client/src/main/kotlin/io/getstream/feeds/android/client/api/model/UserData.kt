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

import io.getstream.feeds.android.network.models.UserResponse
import java.util.Date

/**
 * Model representing a feeds user.
 *
 * @property banned Indicates if the user is banned.
 * @property blockedUserIds A list of user IDs that are blocked by this user.
 * @property createdAt The date and time when the user was created.
 * @property custom A map of custom attributes associated with the user.
 * @property deactivatedAt The date and time when the user was deactivated, if applicable.
 * @property deletedAt The date and time when the user was deleted, if applicable.
 * @property id The unique identifier for the user.
 * @property image The URL of the user's profile image, if available.
 * @property language The preferred language of the user, if specified.
 * @property lastActive The date and time when the user was last active, if available.
 * @property name The name of the user, if available.
 * @property online Indicates if the user is currently online.
 * @property revokeTokensIssuedBefore The date and time before which tokens should be revoked, if
 *   applicable.
 * @property role The role of the user in the system.
 * @property teams A list of team IDs that the user is associated with.
 * @property updatedAt The date and time when the user data was last updated.
 */
public data class UserData(
    public val banned: Boolean,
    public val blockedUserIds: List<String>,
    public val createdAt: Date,
    public val custom: Map<String, Any?>,
    public val deactivatedAt: Date?,
    public val deletedAt: Date?,
    public val id: String,
    public val image: String?,
    public val language: String?,
    public val lastActive: Date?,
    public val name: String?,
    public val online: Boolean,
    public val revokeTokensIssuedBefore: Date?,
    public val role: String,
    public val teams: List<String>,
    public val updatedAt: Date,
)

/** Converts a [UserResponse] to a [UserData] model. */
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
