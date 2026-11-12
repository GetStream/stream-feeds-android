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

/** [What a moderation rule does when it matches.] */
/** ModerationAction Enum */
public sealed class ModerationAction(public val value: String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: String): ModerationAction =
            when (s) {
                "bounce" -> Bounce
                "bounce_flag" -> BounceFlag
                "bounce_remove" -> BounceRemove
                "flag" -> Flag
                "remove" -> Remove
                "shadow" -> Shadow
                else -> Unknown(s)
            }
    }

    public object Bounce : ModerationAction("bounce")

    public object BounceFlag : ModerationAction("bounce_flag")

    public object BounceRemove : ModerationAction("bounce_remove")

    public object Flag : ModerationAction("flag")

    public object Remove : ModerationAction("remove")

    public object Shadow : ModerationAction("shadow")

    public data class Unknown(val unknownValue: String) : ModerationAction(unknownValue)

    public class ModerationActionAdapter : JsonAdapter<ModerationAction>() {
        @FromJson
        override fun fromJson(reader: JsonReader): ModerationAction? {
            val s = reader.nextString() ?: return null
            return ModerationAction.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: ModerationAction?) {
            writer.value(value?.value)
        }
    }
}
