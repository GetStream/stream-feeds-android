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

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class BulkActionAppealsRequest(
    @Json(name = "action_type") public val actionType: ActionType,
    @Json(name = "appeal_ids") public val appealIds: List<String> = emptyList(),
    @Json(name = "mark_reviewed")
    public val markReviewed: io.getstream.feeds.android.network.models.MarkReviewedRequestPayload? =
        null,
    @Json(name = "reject_appeal")
    public val rejectAppeal: io.getstream.feeds.android.network.models.RejectAppealRequestPayload? =
        null,
    @Json(name = "restore")
    public val restore: io.getstream.feeds.android.network.models.RestoreActionRequestPayload? =
        null,
    @Json(name = "unban")
    public val unban: io.getstream.feeds.android.network.models.UnbanActionRequestPayload? = null,
    @Json(name = "unblock")
    public val unblock: io.getstream.feeds.android.network.models.UnblockActionRequestPayload? =
        null,
) {

    /** ActionType Enum */
    public sealed class ActionType(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): ActionType =
                when (s) {
                    "mark_reviewed" -> MarkReviewed
                    "reject_appeal" -> RejectAppeal
                    "restore" -> Restore
                    "unban" -> Unban
                    "unblock" -> Unblock
                    else -> Unknown(s)
                }
        }

        public object MarkReviewed : ActionType("mark_reviewed")

        public object RejectAppeal : ActionType("reject_appeal")

        public object Restore : ActionType("restore")

        public object Unban : ActionType("unban")

        public object Unblock : ActionType("unblock")

        public data class Unknown(val unknownValue: String) : ActionType(unknownValue)

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
