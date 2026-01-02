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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class FollowResponse(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "follower_role") public val followerRole: kotlin.String,
    @Json(name = "push_preference") public val pushPreference: PushPreference,
    @Json(name = "status") public val status: Status,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "source_feed")
    public val sourceFeed: io.getstream.feeds.android.network.models.FeedResponse,
    @Json(name = "target_feed")
    public val targetFeed: io.getstream.feeds.android.network.models.FeedResponse,
    @Json(name = "request_accepted_at") public val requestAcceptedAt: java.util.Date? = null,
    @Json(name = "request_rejected_at") public val requestRejectedAt: java.util.Date? = null,
    @Json(name = "custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
) {

    /** PushPreference Enum */
    public sealed class PushPreference(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): PushPreference =
                when (s) {
                    "all" -> All
                    "none" -> None
                    else -> Unknown(s)
                }
        }

        public object All : PushPreference("all")

        public object None : PushPreference("none")

        public data class Unknown(val unknownValue: kotlin.String) : PushPreference(unknownValue)

        public class PushPreferenceAdapter : JsonAdapter<PushPreference>() {
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
    public sealed class Status(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Status =
                when (s) {
                    "accepted" -> Accepted
                    "pending" -> Pending
                    "rejected" -> Rejected
                    else -> Unknown(s)
                }
        }

        public object Accepted : Status("accepted")

        public object Pending : Status("pending")

        public object Rejected : Status("rejected")

        public data class Unknown(val unknownValue: kotlin.String) : Status(unknownValue)

        public class StatusAdapter : JsonAdapter<Status>() {
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
