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

/** Basic response information */
public data class UpdateFeedMembersResponse(
    @Json(name = "duration") public val duration: kotlin.String,
    @Json(name = "added")
    public val added:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedMemberResponse> =
        emptyList(),
    @Json(name = "removed_ids")
    public val removedIds: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "updated")
    public val updated:
        kotlin.collections.List<io.getstream.feeds.android.network.models.FeedMemberResponse> =
        emptyList(),
)
