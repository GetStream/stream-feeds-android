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

/** [Who can see and follow a feed.] */
/** FeedVisibility Enum */
public sealed class FeedVisibility(public val value: String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: String): FeedVisibility =
            when (s) {
                "followers" -> Followers
                "members" -> Members
                "private" -> Private
                "public" -> Public
                "visible" -> Visible
                else -> Unknown(s)
            }
    }

    public object Followers : FeedVisibility("followers")

    public object Members : FeedVisibility("members")

    public object Private : FeedVisibility("private")

    public object Public : FeedVisibility("public")

    public object Visible : FeedVisibility("visible")

    public data class Unknown(val unknownValue: String) : FeedVisibility(unknownValue)

    public class FeedVisibilityAdapter : JsonAdapter<FeedVisibility>() {
        @FromJson
        override fun fromJson(reader: JsonReader): FeedVisibility? {
            val s = reader.nextString() ?: return null
            return FeedVisibility.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: FeedVisibility?) {
            writer.value(value?.value)
        }
    }
}
