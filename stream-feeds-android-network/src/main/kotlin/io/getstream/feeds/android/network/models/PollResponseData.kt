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
@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.models

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class PollResponseData(
    @Json(name = "allow_answers") public val allowAnswers: kotlin.Boolean,
    @Json(name = "allow_user_suggested_options")
    public val allowUserSuggestedOptions: kotlin.Boolean,
    @Json(name = "answers_count") public val answersCount: kotlin.Int,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "created_by_id") public val createdById: kotlin.String,
    @Json(name = "description") public val description: kotlin.String,
    @Json(name = "enforce_unique_vote") public val enforceUniqueVote: kotlin.Boolean,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "vote_count") public val voteCount: kotlin.Int,
    @Json(name = "voting_visibility") public val votingVisibility: kotlin.String,
    @Json(name = "latest_answers")
    public val latestAnswers:
        kotlin.collections.List<io.getstream.feeds.android.network.models.PollVoteResponseData> =
        emptyList(),
    @Json(name = "options")
    public val options:
        kotlin.collections.List<io.getstream.feeds.android.network.models.PollOptionResponseData> =
        emptyList(),
    @Json(name = "own_votes")
    public val ownVotes:
        kotlin.collections.List<io.getstream.feeds.android.network.models.PollVoteResponseData> =
        emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "latest_votes_by_option")
    public val latestVotesByOption:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<io.getstream.feeds.android.network.models.PollVoteResponseData>,
        > =
        emptyMap(),
    @Json(name = "vote_counts_by_option")
    public val voteCountsByOption: kotlin.collections.Map<kotlin.String, kotlin.Int> = emptyMap(),
    @Json(name = "is_closed") public val isClosed: kotlin.Boolean? = null,
    @Json(name = "max_votes_allowed") public val maxVotesAllowed: kotlin.Int? = null,
    @Json(name = "created_by")
    public val createdBy: io.getstream.feeds.android.network.models.UserResponse? = null,
)
