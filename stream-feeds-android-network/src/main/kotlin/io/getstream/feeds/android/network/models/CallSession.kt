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
public data class CallSession(
    @Json(name = "AnonymousParticipantCount") public val anonymousParticipantCount: kotlin.Int,
    @Json(name = "AppPK") public val appPK: kotlin.Int,
    @Json(name = "CallID") public val callID: kotlin.String,
    @Json(name = "CallType") public val callType: kotlin.String,
    @Json(name = "CreatedAt") public val createdAt: java.util.Date,
    @Json(name = "SessionID") public val sessionID: kotlin.String,
    @Json(name = "ActiveSFUs")
    public val activeSFUs:
        kotlin.collections.List<io.getstream.feeds.android.network.models.SFUIDLastSeen> =
        emptyList(),
    @Json(name = "Participants")
    public val participants:
        kotlin.collections.List<io.getstream.feeds.android.network.models.CallParticipant> =
        emptyList(),
    @Json(name = "SFUIDs") public val sFUIDs: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "AcceptedBy")
    public val acceptedBy: kotlin.collections.Map<kotlin.String, java.util.Date> = emptyMap(),
    @Json(name = "MissedBy")
    public val missedBy: kotlin.collections.Map<kotlin.String, java.util.Date> = emptyMap(),
    @Json(name = "ParticipantsCountByRole")
    public val participantsCountByRole: kotlin.collections.Map<kotlin.String, kotlin.Int> =
        emptyMap(),
    @Json(name = "RejectedBy")
    public val rejectedBy: kotlin.collections.Map<kotlin.String, java.util.Date> = emptyMap(),
    @Json(name = "UserPermissionOverrides")
    public val userPermissionOverrides:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.Map<kotlin.String, kotlin.Boolean>,
        > =
        emptyMap(),
    @Json(name = "DeletedAt") public val deletedAt: java.util.Date? = null,
    @Json(name = "EndedAt") public val endedAt: java.util.Date? = null,
    @Json(name = "LiveEndedAt") public val liveEndedAt: java.util.Date? = null,
    @Json(name = "LiveStartedAt") public val liveStartedAt: java.util.Date? = null,
    @Json(name = "RingAt") public val ringAt: java.util.Date? = null,
    @Json(name = "StartedAt") public val startedAt: java.util.Date? = null,
    @Json(name = "TimerEndsAt") public val timerEndsAt: java.util.Date? = null,
)
