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
public data class FeedRequest(
    @Json(name = "feed_group_id") public val feedGroupId: String,
    @Json(name = "feed_id") public val feedId: String,
    @Json(name = "created_by_id") public val createdById: String? = null,
    @Json(name = "description") public val description: String? = null,
    @Json(name = "name") public val name: String? = null,
    @Json(name = "visibility")
    public val visibility: io.getstream.feeds.android.network.models.FeedVisibility? = null,
    @Json(name = "filter_tags") public val filterTags: List<String>? = null,
    @Json(name = "members")
    public val members: List<io.getstream.feeds.android.network.models.FeedMemberRequest>? = null,
    @Json(name = "custom") public val custom: Map<String, Any?>? = null,
    @Json(name = "location")
    public val location: io.getstream.feeds.android.network.models.Location? = null,
)
