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

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.ReactionGroupData
import io.getstream.feeds.android.client.api.model.increment
import io.getstream.feeds.android.client.internal.utils.upsert

internal inline fun <T> addReaction(
    ownReactions: List<FeedsReactionData>,
    latestReactions: List<FeedsReactionData>,
    reactionGroups: Map<String, ReactionGroupData>,
    reaction: FeedsReactionData,
    currentUserId: String,
    update:
        (
            latestReactions: List<FeedsReactionData>,
            reactionGroups: Map<String, ReactionGroupData>,
            reactionCount: Int,
            ownReactions: List<FeedsReactionData>,
        ) -> T,
): T {
    val ownReaction = reaction.user.id == currentUserId
    val updatedOwnReactions =
        if (ownReaction) {
            ownReactions.upsert(reaction, FeedsReactionData::id)
        } else {
            ownReactions
        }
    val updatedLatestReactions = latestReactions.upsert(reaction, FeedsReactionData::id)
    val inserted =
        updatedOwnReactions.size > ownReactions.size ||
            updatedLatestReactions.size > latestReactions.size
    val reactionGroup =
        reactionGroups[reaction.type]
            ?: ReactionGroupData(count = 1, reaction.createdAt, reaction.createdAt)
    // Increment the count if the reaction was inserted (not an update of existing)
    val updatedReactionGroup =
        if (inserted) {
            reactionGroup.increment(reaction.createdAt)
        } else {
            reactionGroup
        }
    val updatedReactionGroups =
        reactionGroups.toMutableMap().apply { this[reaction.type] = updatedReactionGroup }
    val updatedReactionCount = updatedReactionGroups.values.sumOf(ReactionGroupData::count)
    return update(
        updatedLatestReactions,
        updatedReactionGroups,
        updatedReactionCount,
        updatedOwnReactions,
    )
}
