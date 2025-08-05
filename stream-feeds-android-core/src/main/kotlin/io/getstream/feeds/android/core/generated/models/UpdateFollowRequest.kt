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

data class UpdateFollowRequest (
    @Json(name = "source")
    val source: kotlin.String,

    @Json(name = "target")
    val target: kotlin.String,

    @Json(name = "create_notification_activity")
    val createNotificationActivity: kotlin.Boolean? = null,

    @Json(name = "follower_role")
    val followerRole: kotlin.String? = null,

    @Json(name = "push_preference")
    val pushPreference: PushPreference? = null,

    @Json(name = "custom")
    val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap()
)
{
    
    /**
    * PushPreference Enum
    */
    sealed class PushPreference(val value: kotlin.String) {
            override fun toString(): String = value

            companion object {
                fun fromString(s: kotlin.String): PushPreference = when (s) {
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
}
