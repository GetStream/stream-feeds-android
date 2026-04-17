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
public data class RuleBuilderAction(
    @Json(name = "skip_inbox") public val skipInbox: kotlin.Boolean? = null,
    @Json(name = "type") public val type: Type? = null,
    @Json(name = "ban_options")
    public val banOptions: io.getstream.feeds.android.network.models.BanOptions? = null,
    @Json(name = "flag_user_options")
    public val flagUserOptions: io.getstream.feeds.android.network.models.FlagUserOptions? = null,
) {

    /** Type Enum */
    public sealed class Type(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): Type =
                when (s) {
                    "ban_user" -> BanUser
                    "block_content" -> BlockContent
                    "blur" -> Blur
                    "bounce_content" -> BounceContent
                    "bounce_flag_content" -> BounceFlagContent
                    "bounce_remove_content" -> BounceRemoveContent
                    "call_blur" -> CallBlur
                    "call_warning" -> CallWarning
                    "end_call" -> EndCall
                    "flag_content" -> FlagContent
                    "flag_user" -> FlagUser
                    "kick_user" -> KickUser
                    "mute_audio" -> MuteAudio
                    "mute_video" -> MuteVideo
                    "shadow_content" -> ShadowContent
                    "warning" -> Warning
                    "webhook_only" -> WebhookOnly
                    else -> Unknown(s)
                }
        }

        public object BanUser : Type("ban_user")

        public object BlockContent : Type("block_content")

        public object Blur : Type("blur")

        public object BounceContent : Type("bounce_content")

        public object BounceFlagContent : Type("bounce_flag_content")

        public object BounceRemoveContent : Type("bounce_remove_content")

        public object CallBlur : Type("call_blur")

        public object CallWarning : Type("call_warning")

        public object EndCall : Type("end_call")

        public object FlagContent : Type("flag_content")

        public object FlagUser : Type("flag_user")

        public object KickUser : Type("kick_user")

        public object MuteAudio : Type("mute_audio")

        public object MuteVideo : Type("mute_video")

        public object ShadowContent : Type("shadow_content")

        public object Warning : Type("warning")

        public object WebhookOnly : Type("webhook_only")

        public data class Unknown(val unknownValue: kotlin.String) : Type(unknownValue)

        public class TypeAdapter : JsonAdapter<Type>() {
            @FromJson
            override fun fromJson(reader: JsonReader): Type? {
                val s = reader.nextString() ?: return null
                return Type.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: Type?) {
                writer.value(value?.value)
            }
        }
    }
}
