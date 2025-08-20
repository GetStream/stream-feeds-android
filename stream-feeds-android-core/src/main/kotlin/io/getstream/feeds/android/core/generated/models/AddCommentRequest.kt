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

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
data class AddCommentRequest(
    @Json(name = "comment") val comment: kotlin.String,
    @Json(name = "object_id") val objectId: kotlin.String,
    @Json(name = "object_type") val objectType: kotlin.String,
    @Json(name = "create_notification_activity")
    val createNotificationActivity: kotlin.Boolean? = null,
    @Json(name = "parent_id") val parentId: kotlin.String? = null,
    @Json(name = "attachments")
    val attachments:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.Attachment>? =
        emptyList(),
    @Json(name = "mentioned_user_ids")
    val mentionedUserIds: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "custom") val custom: kotlin.collections.Map<kotlin.String, Any?>? = emptyMap(),
)
