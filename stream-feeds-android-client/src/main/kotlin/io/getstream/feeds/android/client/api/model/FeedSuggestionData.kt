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

import io.getstream.feeds.android.client.api.state.Feed

/**
 * Model representing a feed suggestion
 *
 * @property feed The suggested feed.
 * @property algorithmScores A map of algorithm scores associated with the suggestion.
 * @property reason The reason for the suggestion.
 * @property recommendationScore The overall recommendation score for the suggestion.
 * @see [Feed.queryFollowSuggestions].
 */
public data class FeedSuggestionData(
    public val feed: FeedData,
    public val algorithmScores: Map<String, Float>?,
    public val reason: String?,
    public val recommendationScore: Float?,
)
