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

data class BanActionRequest (
    @Json(name = "channel_ban_only")
    val channelBanOnly: kotlin.Boolean? = null,

    @Json(name = "delete_messages")
    val deleteMessages: DeleteMessages? = null,

    @Json(name = "ip_ban")
    val ipBan: kotlin.Boolean? = null,

    @Json(name = "reason")
    val reason: kotlin.String? = null,

    @Json(name = "shadow")
    val shadow: kotlin.Boolean? = null,

    @Json(name = "timeout")
    val timeout: kotlin.Int? = null
)
{
    
    /**
    * DeleteMessages Enum
    */
    sealed class DeleteMessages(val value: kotlin.String) {
            override fun toString(): String = value

            companion object {
                fun fromString(s: kotlin.String): DeleteMessages = when (s) {
                    "hard" -> Hard
                    "pruning" -> Pruning
                    "soft" -> Soft
                    else -> Unknown(s)
                }
            }
            object Hard : DeleteMessages("hard")
            object Pruning : DeleteMessages("pruning")
            object Soft : DeleteMessages("soft")
            data class Unknown(val unknownValue: kotlin.String) : DeleteMessages(unknownValue)
        

        class DeleteMessagesAdapter : JsonAdapter<DeleteMessages>() {
            @FromJson
            override fun fromJson(reader: JsonReader): DeleteMessages? {
                val s = reader.nextString() ?: return null
                return DeleteMessages.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: DeleteMessages?) {
                writer.value(value?.value)
            }
        }
    }    
}
