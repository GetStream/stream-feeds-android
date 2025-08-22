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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class UpdateFollowRequest(
    @Json(name = "source") public val source: kotlin.String,
    @Json(name = "target") public val target: kotlin.String,
    @Json(name = "create_notification_activity")
    public val createNotificationActivity: kotlin.Boolean? = null,
    @Json(name = "follower_role") public val followerRole: kotlin.String? = null,
    @Json(name = "push_preference") public val pushPreference: PushPreference? = null,
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
}
