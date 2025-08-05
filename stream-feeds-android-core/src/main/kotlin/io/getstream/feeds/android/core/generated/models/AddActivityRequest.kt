/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.*
import kotlin.io.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * 
 */

data class AddActivityRequest (
    @Json(name = "type")
    val type: kotlin.String,

    @Json(name = "fids")
    val fids: kotlin.collections.List<kotlin.String> = emptyList(),

    @Json(name = "expires_at")
    val expiresAt: kotlin.String? = null,

    @Json(name = "id")
    val id: kotlin.String? = null,

    @Json(name = "parent_id")
    val parentId: kotlin.String? = null,

    @Json(name = "poll_id")
    val pollId: kotlin.String? = null,

    @Json(name = "text")
    val text: kotlin.String? = null,

    @Json(name = "visibility")
    val visibility: Visibility? = null,

    @Json(name = "visibility_tag")
    val visibilityTag: kotlin.String? = null,

    @Json(name = "attachments")
    val attachments: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Attachment>? = emptyList(),

    @Json(name = "filter_tags")
    val filterTags: kotlin.collections.List<kotlin.String>? = emptyList(),

    @Json(name = "interest_tags")
    val interestTags: kotlin.collections.List<kotlin.String>? = emptyList(),

    @Json(name = "mentioned_user_ids")
    val mentionedUserIds: kotlin.collections.List<kotlin.String>? = emptyList(),

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),

    @Json(name = "location")
    val location: io.getstream.feeds.android.core.generated.models.ActivityLocation? = null,

    @Json(name = "search_data")
    val searchData: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap()
)
{
    
    /**
    * Visibility Enum
    */
    sealed class Visibility(val value: kotlin.String) {
            override fun toString(): String = value

            companion object {
                fun fromString(s: kotlin.String): Visibility = when (s) {
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
