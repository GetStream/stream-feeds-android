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

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class AddCommentRequest(
    @Json(name = "comment") public val comment: String? = null,
    @Json(name = "copy_custom_to_notification")
    public val copyCustomToNotification: Boolean? = null,
    @Json(name = "create_notification_activity")
    public val createNotificationActivity: Boolean? = null,
    @Json(name = "id") public val id: String? = null,
    @Json(name = "object_id") public val objectId: String? = null,
    @Json(name = "object_type") public val objectType: String? = null,
    @Json(name = "parent_id") public val parentId: String? = null,
    @Json(name = "skip_enrich_url") public val skipEnrichUrl: Boolean? = null,
    @Json(name = "skip_push") public val skipPush: Boolean? = null,
    @Json(name = "attachments")
    public val attachments: List<io.getstream.feeds.android.network.models.Attachment>? = null,
    @Json(name = "mentioned_user_ids") public val mentionedUserIds: List<String>? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
)
