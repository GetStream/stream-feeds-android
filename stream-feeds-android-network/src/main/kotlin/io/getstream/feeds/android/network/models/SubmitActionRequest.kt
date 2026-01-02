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
public data class SubmitActionRequest(
    @Json(name = "action_type") public val actionType: ActionType,
    @Json(name = "item_id") public val itemId: kotlin.String,
    @Json(name = "ban")
    public val ban: io.getstream.feeds.android.network.models.BanActionRequest? = null,
    @Json(name = "block")
    public val block: io.getstream.feeds.android.network.models.BlockActionRequest? = null,
    @Json(name = "custom")
    public val custom: io.getstream.feeds.android.network.models.CustomActionRequest? = null,
    @Json(name = "delete_activity")
    public val deleteActivity: io.getstream.feeds.android.network.models.DeleteActivityRequest? =
        null,
    @Json(name = "delete_comment")
    public val deleteComment: io.getstream.feeds.android.network.models.DeleteCommentRequest? =
        null,
    @Json(name = "delete_message")
    public val deleteMessage: io.getstream.feeds.android.network.models.DeleteMessageRequest? =
        null,
    @Json(name = "delete_reaction")
    public val deleteReaction: io.getstream.feeds.android.network.models.DeleteReactionRequest? =
        null,
    @Json(name = "delete_user")
    public val deleteUser: io.getstream.feeds.android.network.models.DeleteUserRequest? = null,
    @Json(name = "mark_reviewed")
    public val markReviewed: io.getstream.feeds.android.network.models.MarkReviewedRequest? = null,
    @Json(name = "shadow_block")
    public val shadowBlock: io.getstream.feeds.android.network.models.ShadowBlockActionRequest? =
        null,
    @Json(name = "unban")
    public val unban: io.getstream.feeds.android.network.models.UnbanActionRequest? = null,
) {

    /** ActionType Enum */
    public sealed class ActionType(public val value: kotlin.String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: kotlin.String): ActionType =
                when (s) {
                    "ban" -> Ban
                    "block" -> Block
                    "custom" -> Custom
                    "delete_activity" -> DeleteActivity
                    "delete_comment" -> DeleteComment
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

        public object Ban : ActionType("ban")

        public object Block : ActionType("block")

        public object Custom : ActionType("custom")

        public object DeleteActivity : ActionType("delete_activity")

        public object DeleteComment : ActionType("delete_comment")

        public object DeleteMessage : ActionType("delete_message")

        public object DeleteReaction : ActionType("delete_reaction")

        public object DeleteUser : ActionType("delete_user")

        public object EndCall : ActionType("end_call")

        public object KickUser : ActionType("kick_user")

        public object MarkReviewed : ActionType("mark_reviewed")

        public object Restore : ActionType("restore")

        public object ShadowBlock : ActionType("shadow_block")

        public object Unban : ActionType("unban")

        public object Unblock : ActionType("unblock")

        public object Unmask : ActionType("unmask")

        public data class Unknown(val unknownValue: kotlin.String) : ActionType(unknownValue)

        public class ActionTypeAdapter : JsonAdapter<ActionType>() {
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
