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
