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
import kotlin.collections.Map
import kotlin.io.*

/**  */
data class FollowResponse(
    @Json(name = "created_at") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "follower_role") val followerRole: kotlin.String,
    @Json(name = "push_preference") val pushPreference: PushPreference,
    @Json(name = "status") val status: Status,
    @Json(name = "updated_at") val updatedAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "source_feed")
    val sourceFeed: io.getstream.feeds.android.core.generated.models.FeedResponse,
    @Json(name = "target_feed")
    val targetFeed: io.getstream.feeds.android.core.generated.models.FeedResponse,
    @Json(name = "request_accepted_at")
    val requestAcceptedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "request_rejected_at")
    val requestRejectedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "custom") val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
) {

    /** PushPreference Enum */
    sealed class PushPreference(val value: kotlin.String) {
        override fun toString(): String = value

        companion object {
            fun fromString(s: kotlin.String): PushPreference =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        object All : PushPreference("all")

        object None : PushPreference("none")

        data class Unknown(val unknownValue: kotlin.String) : PushPreference(unknownValue)

        class PushPreferenceAdapter : JsonAdapter<PushPreference>() {
            @FromJson
            override fun fromJson(reader: JsonReader): PushPreference? {
                val s = reader.nextString() ?: return null
                return PushPreference.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: PushPreference?) {
                writer.value(value?.value)
            }
        }
    }

    /** Status Enum */
    sealed class Status(val value: kotlin.String) {
        override fun toString(): String = value

        companion object {
            fun fromString(s: kotlin.String): Status =
                when (s) {
                    "accepted" -> Accepted
                    "pending" -> Pending
                    "rejected" -> Rejected
                    else -> Unknown(s)
                }
        }

        object Accepted : Status("accepted")

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
