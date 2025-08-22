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

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import kotlin.collections.*
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class UpdatePollRequest(
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "allow_answers") public val allowAnswers: kotlin.Boolean? = null,
    @Json(name = "allow_user_suggested_options")
    public val allowUserSuggestedOptions: kotlin.Boolean? = null,
    @Json(name = "description") public val description: kotlin.String? = null,
    @Json(name = "enforce_unique_vote") public val enforceUniqueVote: kotlin.Boolean? = null,
    @Json(name = "is_closed") public val isClosed: kotlin.Boolean? = null,
    @Json(name = "max_votes_allowed") public val maxVotesAllowed: kotlin.Int? = null,
    @Json(name = "voting_visibility") public val votingVisibility: VotingVisibility? = null,
    @Json(name = "options")
    public val options:
        kotlin.collections.List<io.getstream.feeds.android.network.models.PollOptionRequest>? =
        emptyList(),
    @Json(name = "Custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
) {

    /** VotingVisibility Enum */
    public sealed class VotingVisibility(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): VotingVisibility =
                when (s) {
                    "anonymous" -> Anonymous
                    "public" -> Public
                    else -> Unknown(s)
                }
        }

        public object Anonymous : VotingVisibility("anonymous")

        public object Public : VotingVisibility("public")

        public data class Unknown(val unknownValue: kotlin.String) : VotingVisibility(unknownValue)

        public class VotingVisibilityAdapter : JsonAdapter<VotingVisibility>() {
            @FromJson
            override fun fromJson(reader: JsonReader): VotingVisibility? {
                val s = reader.nextString() ?: return null
                return VotingVisibility.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: VotingVisibility?) {
                writer.value(value?.value)
            }
        }
    }
}
