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
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.io.*

/**  */
data class CallSession(
    @Json(name = "AnonymousParticipantCount") val anonymousParticipantCount: kotlin.Int,
    @Json(name = "AppPK") val appPK: kotlin.Int,
    @Json(name = "CallID") val callID: kotlin.String,
    @Json(name = "CallType") val callType: kotlin.String,
    @Json(name = "CreatedAt") val createdAt: org.threeten.bp.OffsetDateTime,
    @Json(name = "SessionID") val sessionID: kotlin.String,
    @Json(name = "ActiveSFUs")
    val activeSFUs:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.SFUIDLastSeen> =
        emptyList(),
    @Json(name = "Participants")
    val participants:
        kotlin.collections.List<io.getstream.feeds.android.core.generated.models.CallParticipant> =
        emptyList(),
    @Json(name = "SFUIDs") val sFUIDs: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "AcceptedBy")
    val acceptedBy: kotlin.collections.Map<kotlin.String, org.threeten.bp.OffsetDateTime> =
        emptyMap(),
    @Json(name = "MissedBy")
    val missedBy: kotlin.collections.Map<kotlin.String, org.threeten.bp.OffsetDateTime> =
        emptyMap(),
    @Json(name = "ParticipantsCountByRole")
    val participantsCountByRole: kotlin.collections.Map<kotlin.String, kotlin.Int> = emptyMap(),
    @Json(name = "RejectedBy")
    val rejectedBy: kotlin.collections.Map<kotlin.String, org.threeten.bp.OffsetDateTime> =
        emptyMap(),
    @Json(name = "UserPermissionOverrides")
    val userPermissionOverrides:
        kotlin.collections.Map<
            kotlin.String,
            kotlin.collections.Map<kotlin.String, kotlin.Boolean>,
        > =
        emptyMap(),
    @Json(name = "DeletedAt") val deletedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "EndedAt") val endedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "LiveEndedAt") val liveEndedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "LiveStartedAt") val liveStartedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "RingAt") val ringAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "StartedAt") val startedAt: org.threeten.bp.OffsetDateTime? = null,
    @Json(name = "TimerEndsAt") val timerEndsAt: org.threeten.bp.OffsetDateTime? = null,
)
