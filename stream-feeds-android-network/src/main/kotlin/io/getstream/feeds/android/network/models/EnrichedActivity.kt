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
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class EnrichedActivity(
    @Json(name = "foreign_id") public val foreignId: kotlin.String? = null,
    @Json(name = "id") public val id: kotlin.String? = null,
    @Json(name = "score") public val score: kotlin.Float? = null,
    @Json(name = "verb") public val verb: kotlin.String? = null,
    @Json(name = "to") public val to: kotlin.collections.List<kotlin.String>? = emptyList(),
    @Json(name = "actor") public val actor: io.getstream.feeds.android.network.models.Data? = null,
    @Json(name = "latest_reactions")
    public val latestReactions:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<io.getstream.feeds.android.network.models.EnrichedReaction>,
        >? =
        emptyMap(),
    @Json(name = "object")
    public val `object`: io.getstream.feeds.android.network.models.Data? = null,
    @Json(name = "origin")
    public val origin: io.getstream.feeds.android.network.models.Data? = null,
    @Json(name = "own_reactions")
    public val ownReactions:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.List<io.getstream.feeds.android.network.models.EnrichedReaction>,
        >? =
        emptyMap(),
    @Json(name = "reaction_counts")
    public val reactionCounts: kotlin.collections.Map<kotlin.String, kotlin.Int>? = emptyMap(),
    @Json(name = "target") public val target: io.getstream.feeds.android.network.models.Data? = null,
)
