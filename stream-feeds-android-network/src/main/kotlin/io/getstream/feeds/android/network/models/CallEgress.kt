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

package io.getstream.feeds.android.network.models

import com.squareup.moshi.Json
import kotlin.collections.*
import kotlin.io.*

/**  */
public data class CallEgress(
    @Json(name = "app_pk") public val appPk: kotlin.Int,
    @Json(name = "call_id") public val callId: kotlin.String,
    @Json(name = "call_type") public val callType: kotlin.String,
    @Json(name = "egress_id") public val egressId: kotlin.String,
    @Json(name = "egress_type") public val egressType: kotlin.String,
    @Json(name = "instance_ip") public val instanceIp: kotlin.String,
    @Json(name = "started_at") public val startedAt: java.util.Date,
    @Json(name = "state") public val state: kotlin.String,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "stopped_at") public val stoppedAt: java.util.Date? = null,
    @Json(name = "config")
    public val config: io.getstream.feeds.android.network.models.EgressTaskConfig? = null,
)
