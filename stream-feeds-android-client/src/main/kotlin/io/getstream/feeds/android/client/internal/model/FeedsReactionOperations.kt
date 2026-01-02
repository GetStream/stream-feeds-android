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

import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.internal.utils.insertUniqueBy
import io.getstream.feeds.android.client.internal.utils.insertUniqueBySorted
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.client.internal.utils.upsertSorted
import io.getstream.feeds.android.network.models.FeedsReactionResponse

/**
 * Converts a [io.getstream.feeds.android.network.models.FeedsReactionResponse] to a
 * [io.getstream.feeds.android.client.api.model.FeedsReactionData] model.
 */
internal fun FeedsReactionResponse.toModel(): FeedsReactionData =
    FeedsReactionData(
        activityId = activityId,
        commentId = commentId,
        createdAt = createdAt,
        custom = custom,
        type = type,
        updatedAt = updatedAt,
        user = user.toModel(),
    )

/**
 * Inserts or updates a reaction in the list. If [enforceUnique] is true, it ensures that only one
 * reaction per user exists in the list.
 */
internal fun List<FeedsReactionData>.upsertReaction(
    reaction: FeedsReactionData,
    enforceUnique: Boolean,
): List<FeedsReactionData> =
    if (enforceUnique) {
        insertUniqueBy(reaction, FeedsReactionData::userReactionsGroupId)
    } else {
        upsert(reaction, FeedsReactionData::id)
    }

/**
 * Inserts or updates a reaction in the list keeping the list sorted by [comparator]. If
 * [enforceUnique] is true, it ensures that only one reaction per user exists in the list.
 */
internal fun List<FeedsReactionData>.upsertReactionSorted(
    reaction: FeedsReactionData,
    enforceUnique: Boolean,
    comparator: Comparator<FeedsReactionData>,
): List<FeedsReactionData> =
    if (enforceUnique) {
        insertUniqueBySorted(reaction, FeedsReactionData::userReactionsGroupId, comparator)
    } else {
        upsertSorted(reaction, FeedsReactionData::id, comparator)
    }
