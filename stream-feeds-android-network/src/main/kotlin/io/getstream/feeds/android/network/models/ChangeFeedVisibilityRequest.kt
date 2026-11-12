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
public data class ChangeFeedVisibilityRequest(
    @Json(name = "visibility")
    public val visibility: io.getstream.feeds.android.network.models.FeedVisibility,
    @Json(name = "pending_follows_action")
    public val pendingFollowsAction: PendingFollowsAction? = null,
) {

    /** PendingFollowsAction Enum */
    public sealed class PendingFollowsAction(public val value: String) {
        override fun toString(): String = value

        public companion object {
            public fun fromString(s: String): PendingFollowsAction =
                when (s) {
                    "auto_approve" -> AutoApprove
                    "reject" -> Reject
                    else -> Unknown(s)
                }
        }

        public object AutoApprove : PendingFollowsAction("auto_approve")

        public object Reject : PendingFollowsAction("reject")

        public data class Unknown(val unknownValue: String) : PendingFollowsAction(unknownValue)

        public class PendingFollowsActionAdapter : JsonAdapter<PendingFollowsAction>() {
            @FromJson
            override fun fromJson(reader: JsonReader): PendingFollowsAction? {
                val s = reader.nextString() ?: return null
                return PendingFollowsAction.fromString(s)
            }

            @ToJson
            override fun toJson(writer: JsonWriter, value: PendingFollowsAction?) {
                writer.value(value?.value)
            }
        }
    }
}
