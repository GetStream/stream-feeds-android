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

/**
 * Cursor & depth information for a comment's direct replies. Mirrors Reddit's 'load more replies'
 * semantics.
 */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class RepliesMeta(
    @Json(name = "depth_truncated") public val depthTruncated: Boolean,
    @Json(name = "has_more") public val hasMore: Boolean,
    @Json(name = "remaining") public val remaining: Int,
    @Json(name = "next_cursor") public val nextCursor: String? = null,
)
