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

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.collections.List
import kotlin.io.*

/**  */
public data class UserMutedEvent(
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "type") public val type: kotlin.String,
    @Json(name = "target_user") public val targetUser: kotlin.String? = null,
    @Json(name = "target_users")
    public val targetUsers: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "user") public val user: io.getstream.feeds.android.network.models.User? = null,
) :
    io.getstream.feeds.android.network.models.WSEvent,
    io.getstream.feeds.android.network.models.FeedEvent {

    override fun getWSEventType(): kotlin.String {
        return type
    }
}
