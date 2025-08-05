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

data class FeedMemberResponse (
    @Json(name = "created_at")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "role")
    val role: kotlin.String,

    @Json(name = "status")
    val status: Status,

    @Json(name = "updated_at")
    val updatedAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "user")
    val user: io.getstream.feeds.android.core.generated.models.UserResponse,

    @Json(name = "invite_accepted_at")
    val inviteAcceptedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "invite_rejected_at")
    val inviteRejectedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap()
)
{
    
    /**
    * Status Enum
    */
    sealed class Status(val value: kotlin.String) {
            override fun toString(): String = value

            companion object {
                fun fromString(s: kotlin.String): Status = when (s) {
                    "member" -> Member
                    "pending" -> Pending
                    "rejected" -> Rejected
                    else -> Unknown(s)
                }
            }
            object Member : Status("member")
            object Pending : Status("pending")
            object Rejected : Status("rejected")
            data class Unknown(val unknownValue: kotlin.String) : Status(unknownValue)
        

        class StatusAdapter : JsonAdapter<Status>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Status? {
                val s = reader.nextString() ?: return null
                return Status.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Status?) {
                writer.value(value?.value)
            }
        }
    }    
}
