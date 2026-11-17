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
public data class PollResponseData(
    @Json(name = "allow_answers") public val allowAnswers: Boolean,
    @Json(name = "allow_user_suggested_options") public val allowUserSuggestedOptions: Boolean,
    @Json(name = "answers_count") public val answersCount: Int,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "created_by_id") public val createdById: String,
    @Json(name = "description") public val description: String,
    @Json(name = "enforce_unique_vote") public val enforceUniqueVote: Boolean,
    @Json(name = "id") public val id: String,
    @Json(name = "name") public val name: String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "vote_count") public val voteCount: Int,
    @Json(name = "voting_visibility")
    public val votingVisibility: io.getstream.feeds.android.network.models.VotingVisibility,
    @Json(name = "latest_answers")
    public val latestAnswers: List<io.getstream.feeds.android.network.models.PollVoteResponseData> =
        emptyList(),
    @Json(name = "options")
    public val options: List<io.getstream.feeds.android.network.models.PollOptionResponseData> =
        emptyList(),
    @Json(name = "own_votes")
    public val ownVotes: List<io.getstream.feeds.android.network.models.PollVoteResponseData> =
        emptyList(),
    @Json(name = "custom") public val custom: Map<String, Any?> = emptyMap(),
    @Json(name = "latest_votes_by_option")
    public val latestVotesByOption:
        Map<String, List<io.getstream.feeds.android.network.models.PollVoteResponseData>> =
        emptyMap(),
    @Json(name = "vote_counts_by_option")
    public val voteCountsByOption: Map<String, Int> = emptyMap(),
    @Json(name = "is_closed") public val isClosed: Boolean? = null,
    @Json(name = "max_votes_allowed") public val maxVotesAllowed: Int? = null,
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponse? = null,
    @Json(name = "description_i18n") public val descriptionI18n: Map<String, String>? = null,
    @Json(name = "name_i18n") public val nameI18n: Map<String, String>? = null,
)
