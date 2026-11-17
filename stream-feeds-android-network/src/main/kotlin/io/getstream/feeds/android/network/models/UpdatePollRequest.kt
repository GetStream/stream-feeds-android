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

@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.models

import com.squareup.moshi.Json

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class UpdatePollRequest(
    @Json(name = "id") public val id: String,
    @Json(name = "name") public val name: String,
    @Json(name = "allow_answers") public val allowAnswers: Boolean? = null,
    @Json(name = "allow_user_suggested_options")
    public val allowUserSuggestedOptions: Boolean? = null,
    @Json(name = "description") public val description: String? = null,
    @Json(name = "enforce_unique_vote") public val enforceUniqueVote: Boolean? = null,
    @Json(name = "is_closed") public val isClosed: Boolean? = null,
    @Json(name = "max_votes_allowed") public val maxVotesAllowed: Int? = null,
    @Json(name = "voting_visibility")
    public val votingVisibility: io.getstream.feeds.android.network.models.VotingVisibility? = null,
    @Json(name = "options")
    public val options: List<io.getstream.feeds.android.network.models.PollOptionRequest>? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
)
