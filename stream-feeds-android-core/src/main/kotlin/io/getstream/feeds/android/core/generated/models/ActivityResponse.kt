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

package io.getstream.feeds.android.core.generated.models

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
data class ActivityResponse(
    @Json(name = "bookmark_count") val bookmarkCount: kotlin.Int,
    @Json(name = "comment_count") val commentCount: kotlin.Int,
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "id") val id: kotlin.String,
    @Json(name = "popularity") val popularity: kotlin.Int,
    @Json(name = "reaction_count") val reactionCount: kotlin.Int,
    @Json(name = "score") val score: kotlin.Float,
    @Json(name = "share_count") val shareCount: kotlin.Int,
    @Json(name = "type") val type: kotlin.String,
    @Json(name = "updated_at") val updatedAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "visibility") val visibility: Visibility,
    @Json(name = "attachments")
    val attachments:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Attachment> =
        emptyList(),
    @Json(name = "comments")
    val comments:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.CommentResponse> =
        emptyList(),
    @Json(name = "feeds") val feeds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "filter_tags")
    val filterTags: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "interest_tags")
    val interestTags: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "latest_reactions")
    val latestReactions:
        kotlin.collections.List<
            io.getstream.feeds.android.core.generated.models.FeedsReactionResponse
        > =
        emptyList(),
    @Json(name = "mentioned_users")
    val mentionedUsers:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.UserResponse> =
        emptyList(),
    @Json(name = "own_bookmarks")
    val ownBookmarks:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.BookmarkResponse> =
        emptyList(),
    @Json(name = "own_reactions")
    val ownReactions:
        kotlin.collections.List<
            io.getstream.feeds.android.core.generated.models.FeedsReactionResponse
        > =
        emptyList(),
    @Json(name = "custom") val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "reaction_groups")
    val reactionGroups:
        kotlin.collections.Map<
            kotlin.String,
            io.getstream.feeds.android.core.generated.models.ReactionGroupResponse,
        > =
        emptyMap(),
    @Json(name = "search_data")
    val searchData: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "user") val user: io.getstream.feeds.android.core.generated.models.UserResponse,
    @Json(name = "deleted_at") val deletedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "edited_at") val editedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "expires_at") val expiresAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "hidden") val hidden: kotlin.Boolean? = null,
    @Json(name = "text") val text: kotlin.String? = null,
    @Json(name = "visibility_tag") val visibilityTag: kotlin.String? = null,
    @Json(name = "current_feed")
    val currentFeed: io.getstream.feeds.android.core.generated.models.FeedResponse? = null,
    @Json(name = "location")
    val location: io.getstream.feeds.android.core.generated.models.ActivityLocation? = null,
    @Json(name = "moderation")
    val moderation: io.getstream.feeds.android.core.generated.models.ModerationV2Response? = null,
    @Json(name = "notification_context")
    val notificationContext: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
    @Json(name = "parent")
    val parent: io.getstream.feeds.android.core.generated.models.ActivityResponse? = null,
    @Json(name = "poll")
    val poll: io.getstream.feeds.android.core.generated.models.PollResponseData? = null,
) {

    /** Visibility Enum */
    sealed class Visibility(val value: kotlin.String) {
        override fun toString(): String = value

        companion object {
            fun fromString(s: kotlin.String): Visibility =
                when (s) {
                    "private" -> Private
                    "public" -> Public
                    "tag" -> Tag
                    else -> Unknown(s)
                }
        }

        object Private : Visibility("private")

        object Public : Visibility("public")

        object Tag : Visibility("tag")

        data class Unknown(val unknownValue: kotlin.String) : Visibility(unknownValue)

        class VisibilityAdapter : JsonAdapter<Visibility>() {
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
