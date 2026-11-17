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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/** [Who can see an activity.] */
/** ActivityVisibility Enum */
public sealed class ActivityVisibility(public val value: String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: String): ActivityVisibility =
            when (s) {
                "private" -> Private
                "public" -> Public
                "tag" -> Tag
                else -> Unknown(s)
            }
    }

    public object Private : ActivityVisibility("private")

    public object Public : ActivityVisibility("public")

    public object Tag : ActivityVisibility("tag")

    public data class Unknown(val unknownValue: String) : ActivityVisibility(unknownValue)

    public class ActivityVisibilityAdapter : JsonAdapter<ActivityVisibility>() {
        @FromJson
        override fun fromJson(reader: JsonReader): ActivityVisibility? {
            val s = reader.nextString() ?: return null
            return ActivityVisibility.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: ActivityVisibility?) {
            writer.value(value?.value)
        }
    }
}
