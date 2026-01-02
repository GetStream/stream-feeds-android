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
public data class VelocityFilterConfigRule(
    @Json(name = "action") public val action: Action,
    @Json(name = "ban_duration") public val banDuration: kotlin.Int,
    @Json(name = "cascading_action") public val cascadingAction: CascadingAction,
    @Json(name = "cascading_threshold") public val cascadingThreshold: kotlin.Int,
    @Json(name = "check_message_context") public val checkMessageContext: kotlin.Boolean,
    @Json(name = "fast_spam_threshold") public val fastSpamThreshold: kotlin.Int,
    @Json(name = "fast_spam_ttl") public val fastSpamTtl: kotlin.Int,
    @Json(name = "ip_ban") public val ipBan: kotlin.Boolean,
    @Json(name = "probation_period") public val probationPeriod: kotlin.Int,
    @Json(name = "shadow_ban") public val shadowBan: kotlin.Boolean,
    @Json(name = "slow_spam_threshold") public val slowSpamThreshold: kotlin.Int,
    @Json(name = "slow_spam_ttl") public val slowSpamTtl: kotlin.Int,
    @Json(name = "url_only") public val urlOnly: kotlin.Boolean,
    @Json(name = "slow_spam_ban_duration") public val slowSpamBanDuration: kotlin.Int? = null,
) {

    /** Action Enum */
    public sealed class Action(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Action =
                when (s) {
                    "ban" -> Ban
                    "flag" -> Flag
                    "remove" -> Remove
                    "shadow" -> Shadow
                    else -> Unknown(s)
                }
        }

        public object Ban : Action("ban")

        public object Flag : Action("flag")

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

    /** CascadingAction Enum */
    public sealed class CascadingAction(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): CascadingAction =
                when (s) {
                    "ban" -> Ban
                    "flag" -> Flag
                    "remove" -> Remove
                    "shadow" -> Shadow
                    else -> Unknown(s)
                }
        }

        public object Ban : CascadingAction("ban")

        public object Flag : CascadingAction("flag")

        public object Remove : CascadingAction("remove")

        public object Shadow : CascadingAction("shadow")

        public data class Unknown(val unknownValue: kotlin.String) : CascadingAction(unknownValue)

        public class CascadingActionAdapter : JsonAdapter<CascadingAction>() {
            @FromJson
            override fun fromJson(reader: JsonReader): CascadingAction? {
                val s = reader.nextString() ?: return null
                return CascadingAction.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: CascadingAction?) {
                writer.value(value?.value)
            }
        }
    }
}
