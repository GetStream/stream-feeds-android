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

package io.getstream.feeds.android.client.api.model

import java.util.Date

/**
 * Data class representing a feed reaction.
 *
 * @property activityId The ID of the activity this reaction is associated with.
 * @property createdAt The date and time when the reaction was created.
 * @property custom Optional custom data as a map.
 * @property type The type of the reaction.
 * @property updatedAt The date and time when the reaction was last updated.
 * @property user The user who made the reaction.
 */
public data class FeedsReactionData(
    val activityId: String,
    val commentId: String?,
    val createdAt: Date,
    val custom: Map<String, Any?>?,
    val type: String,
    val updatedAt: Date,
    val user: UserData,
) {

    /** Unique identifier for the reaction. */
    public val id: String = "${activityId}${commentId}${user.id}${type}"

    /** Identifier for grouping a user's reactions. */
    internal val userReactionsGroupId: String = "${activityId}${commentId}${user.id}"
}
