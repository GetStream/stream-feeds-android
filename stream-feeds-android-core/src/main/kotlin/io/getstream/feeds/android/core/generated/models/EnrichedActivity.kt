/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.*
import kotlin.io.*
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

/**
 * 
 */

data class EnrichedActivity (
    @Json(name = "foreign_id")
    val foreignId: kotlin.String? = null,

    @Json(name = "id")
    val id: kotlin.String? = null,

    @Json(name = "score")
    val score: kotlin.Float? = null,

    @Json(name = "verb")
    val verb: kotlin.String? = null,

    @Json(name = "to")
    val to: kotlin.collections.List<kotlin.String>? = emptyList(),

    @Json(name = "actor")
    val actor: io.getstream.feeds.android.core.generated.models.Data? = null,

    @Json(name = "latest_reactions")
    val latestReactions: kotlin.collections.Map<kotlin.String, kotlin.collections.List<io.getstream.feeds.android.core.generated.models.EnrichedReaction>>? = emptyMap(),

    @Json(name = "object")
    val `object`: io.getstream.feeds.android.core.generated.models.Data? = null,

    @Json(name = "origin")
    val origin: io.getstream.feeds.android.core.generated.models.Data? = null,

    @Json(name = "own_reactions")
    val ownReactions: kotlin.collections.Map<kotlin.String, kotlin.collections.List<io.getstream.feeds.android.core.generated.models.EnrichedReaction>>? = emptyMap(),

    @Json(name = "reaction_counts")
    val reactionCounts: kotlin.collections.Map<kotlin.String, kotlin.Int>? = emptyMap(),

    @Json(name = "target")
    val target: io.getstream.feeds.android.core.generated.models.Data? = null
)
