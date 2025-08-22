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
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
public data class CallSessionResponse(
    @Json(name = "anonymous_participant_count") public val anonymousParticipantCount: kotlin.Int,
    @Json(name = "id") public val id: kotlin.String,
    @Json(name = "participants")
    public val participants:
        kotlin.collections.List<io.getstream.feeds.android.network.models.CallParticipantResponse> =
        emptyList(),
    @Json(name = "accepted_by")
    public val acceptedBy: kotlin.collections.Map<kotlin.String, java.util.Date> = emptyMap(),
    @Json(name = "missed_by")
    public val missedBy: kotlin.collections.Map<kotlin.String, java.util.Date> = emptyMap(),
    @Json(name = "participants_count_by_role")
    public val participantsCountByRole: kotlin.collections.Map<kotlin.String, kotlin.Int> =
        emptyMap(),
    @Json(name = "rejected_by")
    public val rejectedBy: kotlin.collections.Map<kotlin.String, java.util.Date> = emptyMap(),
    @Json(name = "ended_at") public val endedAt: java.util.Date? = null,
    @Json(name = "live_ended_at") public val liveEndedAt: java.util.Date? = null,
    @Json(name = "live_started_at") public val liveStartedAt: java.util.Date? = null,
    @Json(name = "started_at") public val startedAt: java.util.Date? = null,
    @Json(name = "timer_ends_at") public val timerEndsAt: java.util.Date? = null,
)
