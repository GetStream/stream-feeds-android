/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
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

package io.getstream.feeds.android.core.generated.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import kotlin.collections.*
import kotlin.io.*

/**  */
data class SubmitActionRequest(
    @Json(name = "action_type") val actionType: ActionType,
    @Json(name = "item_id") val itemId: kotlin.String,
    @Json(name = "ban")
    val ban: io.getstream.feeds.android.core.generated.models.BanActionRequest? = null,
    @Json(name = "custom")
    val custom: io.getstream.feeds.android.core.generated.models.CustomActionRequest? = null,
    @Json(name = "delete_activity")
    val deleteActivity: io.getstream.feeds.android.core.generated.models.DeleteActivityRequest? =
        null,
    @Json(name = "delete_message")
    val deleteMessage: io.getstream.feeds.android.core.generated.models.DeleteMessageRequest? =
        null,
    @Json(name = "delete_reaction")
    val deleteReaction: io.getstream.feeds.android.core.generated.models.DeleteReactionRequest? =
        null,
    @Json(name = "delete_user")
    val deleteUser: io.getstream.feeds.android.core.generated.models.DeleteUserRequest? = null,
    @Json(name = "mark_reviewed")
    val markReviewed: io.getstream.feeds.android.core.generated.models.MarkReviewedRequest? = null,
    @Json(name = "unban")
    val unban: io.getstream.feeds.android.core.generated.models.UnbanActionRequest? = null,
) {

    /** ActionType Enum */
    sealed class ActionType(val value: kotlin.String) {
        override fun toString(): String = value

        companion object {
            fun fromString(s: kotlin.String): ActionType =
                when (s) {
                    "ban" -> Ban
                    "custom" -> Custom
                    "delete_activity" -> DeleteActivity
                    "delete_message" -> DeleteMessage
                    "delete_reaction" -> DeleteReaction
                    "delete_user" -> DeleteUser
                    "end_call" -> EndCall
                    "kick_user" -> KickUser
                    "mark_reviewed" -> MarkReviewed
                    "restore" -> Restore
                    "shadow_block" -> ShadowBlock
                    "unban" -> Unban
                    "unblock" -> Unblock
                    "unmask" -> Unmask
                    else -> Unknown(s)
                }
        }

        object Ban : ActionType("ban")

        object Custom : ActionType("custom")

        object DeleteActivity : ActionType("delete_activity")

        object DeleteMessage : ActionType("delete_message")

        object DeleteReaction : ActionType("delete_reaction")

        object DeleteUser : ActionType("delete_user")

        object EndCall : ActionType("end_call")

        object KickUser : ActionType("kick_user")

        object MarkReviewed : ActionType("mark_reviewed")

        object Restore : ActionType("restore")

        object ShadowBlock : ActionType("shadow_block")

        object Unban : ActionType("unban")

        object Unblock : ActionType("unblock")

        object Unmask : ActionType("unmask")

        data class Unknown(val unknownValue: kotlin.String) : ActionType(unknownValue)

        class ActionTypeAdapter : JsonAdapter<ActionType>() {
            @FromJson
            override fun fromJson(reader: JsonReader): ActionType? {
                val s = reader.nextString() ?: return null
                return ActionType.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: ActionType?) {
                writer.value(value?.value)
            }
        }
    }
}
