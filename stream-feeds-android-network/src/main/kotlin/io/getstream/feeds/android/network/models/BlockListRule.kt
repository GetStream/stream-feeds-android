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
import kotlin.io.*

/**  */
public data class BlockListRule(
    @Json(name = "action") public val action: Action,
    @Json(name = "name") public val name: kotlin.String,
    @Json(name = "team") public val team: kotlin.String,
) {

    /** Action Enum */
    public sealed class Action(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Action =
                when (s) {
                    "bounce" -> Bounce
                    "bounce_flag" -> BounceFlag
                    "bounce_remove" -> BounceRemove
                    "flag" -> Flag
                    "mask_flag" -> MaskFlag
                    "remove" -> Remove
                    "shadow" -> Shadow
                    else -> Unknown(s)
                }
        }

        public object Bounce : Action("bounce")

        public object BounceFlag : Action("bounce_flag")

        public object BounceRemove : Action("bounce_remove")

        public object Flag : Action("flag")

        public object MaskFlag : Action("mask_flag")

        public object Remove : Action("remove")

        public object Shadow : Action("shadow")

        public data class Unknown(val unknownValue: kotlin.String) : Action(unknownValue)

        public class ActionAdapter : JsonAdapter<Action>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Action? {
                val s = reader.nextString() ?: return null
                return Action.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Action?) {
                writer.value(value?.value)
            }
        }
    }
}
