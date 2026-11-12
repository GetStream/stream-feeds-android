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
public data class AddActivityRequest(
    @Json(name = "type") public val type: String,
    @Json(name = "feeds") public val feeds: List<String> = emptyList(),
    @Json(name = "copy_custom_to_notification")
    public val copyCustomToNotification: Boolean? = null,
    @Json(name = "create_notification_activity")
    public val createNotificationActivity: Boolean? = null,
    @Json(name = "enrich_own_fields") public val enrichOwnFields: Boolean? = null,
    @Json(name = "expires_at") public val expiresAt: String? = null,
    @Json(name = "id") public val id: String? = null,
    @Json(name = "parent_id") public val parentId: String? = null,
    @Json(name = "poll_id") public val pollId: String? = null,
    @Json(name = "restrict_replies")
    public val restrictReplies: io.getstream.feeds.android.network.models.RestrictReplies? = null,
    @Json(name = "skip_enrich_url") public val skipEnrichUrl: Boolean? = null,
    @Json(name = "skip_push") public val skipPush: Boolean? = null,
    @Json(name = "text") public val text: String? = null,
    @Json(name = "visibility")
    public val visibility: io.getstream.feeds.android.network.models.ActivityVisibility? = null,
    @Json(name = "visibility_tag") public val visibilityTag: String? = null,
    @Json(name = "attachments")
    public val attachments: List<io.getstream.feeds.android.network.models.Attachment>? = null,
    @Json(name = "collection_refs") public val collectionRefs: List<String>? = null,
    @Json(name = "collections")
    public val collections: List<io.getstream.feeds.android.network.models.CollectionRequest>? =
        null,
    @Json(name = "filter_tags") public val filterTags: List<String>? = null,
    @Json(name = "interest_tags") public val interestTags: List<String>? = null,
    @Json(name = "mentioned_user_ids") public val mentionedUserIds: List<String>? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
    @Json(name = "location")
    public val location: io.getstream.feeds.android.network.models.Location? = null,
    @Json(name = "search_data") public val searchData: Map<String, Any?>? = null,
)
