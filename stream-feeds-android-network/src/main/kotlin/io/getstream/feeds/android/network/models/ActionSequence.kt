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
import kotlin.io.*

/**  */
public data class ActionSequence(
    @Json(name = "action") public val action: kotlin.String,
    @Json(name = "blur") public val blur: kotlin.Boolean,
    @Json(name = "cooldown_period") public val cooldownPeriod: kotlin.Int,
    @Json(name = "threshold") public val threshold: kotlin.Int,
    @Json(name = "time_window") public val timeWindow: kotlin.Int,
    @Json(name = "warning") public val warning: kotlin.Boolean,
    @Json(name = "warning_text") public val warningText: kotlin.String,
)
