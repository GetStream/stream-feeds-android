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
public data class AddActivityRequest(
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "feeds") public val feeds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "expires_at") public val expiresAt: kotlin.String? = null,
    @Json(name = "id") public val id: kotlin.String? = null,
    @Json(name = "parent_id") public val parentId: kotlin.String? = null,
    @Json(name = "poll_id") public val pollId: kotlin.String? = null,
    @Json(name = "restrict_replies") public val restrictReplies: RestrictReplies? = null,
    @Json(name = "skip_enrich_url") public val skipEnrichUrl: kotlin.Boolean? = null,
    @Json(name = "text") public val text: kotlin.String? = null,
    @Json(name = "visibility") public val visibility: Visibility? = null,
    @Json(name = "visibility_tag") public val visibilityTag: kotlin.String? = null,
    @Json(name = "attachments")
    public val attachments:
        kotlin.collections.List<io.getstream.feeds.android.network.models.Attachment>? =
        emptyList(),
    @Json(name = "collection_refs")
    public val collectionRefs: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "filter_tags")
    public val filterTags: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "interest_tags")
    public val interestTags: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "mentioned_user_ids")
    public val mentionedUserIds: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "location")
    public val location: io.getstream.feeds.android.network.models.ActivityLocation? = null,
    @Json(name = "search_data")
    public val searchData: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
) {

    /** RestrictReplies Enum */
    public sealed class RestrictReplies(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): RestrictReplies =
                when (s) {
                    "everyone" -> Everyone
                    "nobody" -> Nobody
                    "people_i_follow" -> PeopleIFollow
                    else -> Unknown(s)
                }
        }

        public object Everyone : RestrictReplies("everyone")

        public object Nobody : RestrictReplies("nobody")

        public object PeopleIFollow : RestrictReplies("people_i_follow")

        public data class Unknown(val unknownValue: kotlin.String) : RestrictReplies(unknownValue)

        public class RestrictRepliesAdapter : JsonAdapter<RestrictReplies>() {
            @FromJson
            override fun fromJson(reader: JsonReader): RestrictReplies? {
                val s = reader.nextString() ?: return null
                return RestrictReplies.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: RestrictReplies?) {
                writer.value(value?.value)
            }
        }
    }

    /** Visibility Enum */
    public sealed class Visibility(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Visibility =
                when (s) {
                    "private" -> Private
                    "public" -> Public
                    "tag" -> Tag
                    else -> Unknown(s)
                }
        }

        public object Private : Visibility("private")

        public object Public : Visibility("public")

        public object Tag : Visibility("tag")

        public data class Unknown(val unknownValue: kotlin.String) : Visibility(unknownValue)

        public class VisibilityAdapter : JsonAdapter<Visibility>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Visibility? {
                val s = reader.nextString() ?: return null
                return Visibility.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Visibility?) {
                writer.value(value?.value)
            }
        }
    }
}
