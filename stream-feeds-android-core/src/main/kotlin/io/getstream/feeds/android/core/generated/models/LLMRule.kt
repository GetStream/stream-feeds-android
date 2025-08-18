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

data class LLMRule (
    @Json(name = "action")
    val action: Action,

    @Json(name = "description")
    val description: kotlin.String,

    @Json(name = "label")
    val label: kotlin.String,

    @Json(name = "severity_rules")
    val severityRules: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.BodyguardSeverityRule> = emptyList()
)
{
    
    /**
    * Action Enum
    */
    sealed class Action(val value: kotlin.String) {
            override fun toString(): String = value

            companion object {
                fun fromString(s: kotlin.String): Action = when (s) {
                    "bounce" -> Bounce
                    "bounce_flag" -> BounceFlag
                    "bounce_remove" -> BounceRemove
                    "flag" -> Flag
                    "keep" -> Keep
                    "remove" -> Remove
                    "shadow" -> Shadow
                    else -> Unknown(s)
                }
            }
            object Bounce : Action("bounce")
            object BounceFlag : Action("bounce_flag")
            object BounceRemove : Action("bounce_remove")
            object Flag : Action("flag")
            object Keep : Action("keep")
            object Remove : Action("remove")
            object Shadow : Action("shadow")
            data class Unknown(val unknownValue: kotlin.String) : Action(unknownValue)
        

        class ActionAdapter : JsonAdapter<Action>() {
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
