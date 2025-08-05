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

data class CallEgress (
    @Json(name = "app_pk")
    val appPk: kotlin.Int,

    @Json(name = "call_id")
    val callId: kotlin.String,

    @Json(name = "call_type")
    val callType: kotlin.String,

    @Json(name = "egress_id")
    val egressId: kotlin.String,

    @Json(name = "egress_type")
    val egressType: kotlin.String,

    @Json(name = "instance_ip")
    val instanceIp: kotlin.String,

    @Json(name = "started_at")
    val startedAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "state")
    val state: kotlin.String,

    @Json(name = "updated_at")
    val updatedAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "stopped_at")
    val stoppedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "config")
    val config: io.getstream.feeds.android.core.generated.models.EgressTaskConfig? = null
)
