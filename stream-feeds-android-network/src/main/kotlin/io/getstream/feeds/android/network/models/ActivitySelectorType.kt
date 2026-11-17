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

/** [How a feed group selects the activities it shows.] */
/** ActivitySelectorType Enum */
public sealed class ActivitySelectorType(public val value: String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: String): ActivitySelectorType =
            when (s) {
                "current_feed" -> CurrentFeed
                "follow_suggestion" -> FollowSuggestion
                "following" -> Following
                "friends_engaged" -> FriendsEngaged
                "interest" -> Interest
                "popular" -> Popular
                "proximity" -> Proximity
                "query" -> Query
                else -> Unknown(s)
            }
    }

    public object CurrentFeed : ActivitySelectorType("current_feed")

    public object FollowSuggestion : ActivitySelectorType("follow_suggestion")

    public object Following : ActivitySelectorType("following")

    public object FriendsEngaged : ActivitySelectorType("friends_engaged")

    public object Interest : ActivitySelectorType("interest")

    public object Popular : ActivitySelectorType("popular")

    public object Proximity : ActivitySelectorType("proximity")

    public object Query : ActivitySelectorType("query")

    public data class Unknown(val unknownValue: String) : ActivitySelectorType(unknownValue)

    public class ActivitySelectorTypeAdapter : JsonAdapter<ActivitySelectorType>() {
        @FromJson
        override fun fromJson(reader: JsonReader): ActivitySelectorType? {
            val s = reader.nextString() ?: return null
            return ActivitySelectorType.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: ActivitySelectorType?) {
            writer.value(value?.value)
        }
    }
}
