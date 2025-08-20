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

import io.getstream.feeds.android.client.internal.model.mapping.toDate
import io.getstream.feeds.android.core.generated.models.ReactionGroupResponse
import java.util.Date

/**
 * Data class representing a group of reactions.
 *
 * @property count The number of reactions in the group.
 * @property firstReactionAt The date and time of the first reaction.
 * @property lastReactionAt The date and time of the last reaction.
 */
public data class ReactionGroupData(
    val count: Int,
    val firstReactionAt: Date,
    val lastReactionAt: Date,
)

/** Returns true if the reaction group is empty (count is 0 or less). */
internal val ReactionGroupData.isEmpty: Boolean
    get() = count <= 0

/**
 * Returns a copy of this ReactionGroupData with the count decremented by 1 (not below 0), if the
 * given date is within the valid range (date >= firstReactionAt or date <= lastReactionAt).
 * Otherwise, returns the original instance.
 *
 * @param date The date to check for decrementing.
 * @return A new ReactionGroupData with updated count, or the original if not decremented.
 */
internal fun ReactionGroupData.decrement(date: Date): ReactionGroupData {
    return if ((date >= firstReactionAt) || (date <= lastReactionAt)) {
        this.copy(count = maxOf(0, count - 1))
    } else {
        this
    }
}

/**
 * Returns a copy of this ReactionGroupData with the count incremented by 1 and lastReactionAt
 * updated to the given date, if the date is after firstReactionAt. Otherwise, returns the original
 * instance.
 *
 * @param date The date to use for incrementing and updating lastReactionAt.
 * @return A new ReactionGroupData with updated count and lastReactionAt, or the original if not
 *   incremented.
 */
internal fun ReactionGroupData.increment(date: Date): ReactionGroupData {
    return if (date > firstReactionAt) {
        this.copy(count = count + 1, lastReactionAt = date)
    } else {
        this
    }
}

/** Converts a [ReactionGroupResponse] to a [ReactionGroupData] model. */
internal fun ReactionGroupResponse.toModel(): ReactionGroupData =
    ReactionGroupData(
        count = count,
        firstReactionAt = firstReactionAt.toDate(),
        lastReactionAt = lastReactionAt.toDate(),
    )
