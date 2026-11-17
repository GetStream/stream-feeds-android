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

/** [Who is allowed to reply to an activity.] */
/** RestrictReplies Enum */
public sealed class RestrictReplies(public val value: String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: String): RestrictReplies =
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

    public data class Unknown(val unknownValue: String) : RestrictReplies(unknownValue)

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
