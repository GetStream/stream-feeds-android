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
import kotlin.io.*

/**
 * Cursor & depth information for a comment's direct replies. Mirrors Reddit's 'load more replies'
 * semantics.
 */
data class RepliesMeta(
    @Json(name = "depth_truncated") val depthTruncated: kotlin.Boolean,
    @Json(name = "has_more") val hasMore: kotlin.Boolean,
    @Json(name = "remaining") val remaining: kotlin.Int,
    @Json(name = "next_cursor") val nextCursor: kotlin.String? = null,
)
