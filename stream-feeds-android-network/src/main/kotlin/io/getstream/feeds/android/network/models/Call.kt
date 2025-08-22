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
public data class Call(
    @Json(name = "AppPK") public val appPK: kotlin.Int,
    @Json(name = "Backstage") public val backstage: kotlin.Boolean,
    @Json(name = "CID") public val cID: kotlin.String,
    @Json(name = "ChannelCID") public val channelCID: kotlin.String,
    @Json(name = "CreatedAt") public val createdAt: java.util.Date,
    @Json(name = "CreatedByUserID") public val createdByUserID: kotlin.String,
    @Json(name = "CurrentSessionID") public val currentSessionID: kotlin.String,
    @Json(name = "ID") public val iD: kotlin.String,
    @Json(name = "LastSessionID") public val lastSessionID: kotlin.String,
    @Json(name = "Team") public val team: kotlin.String,
    @Json(name = "ThumbnailURL") public val thumbnailURL: kotlin.String,
    @Json(name = "Type") public val type: kotlin.String,
    @Json(name = "UpdatedAt") public val updatedAt: java.util.Date,
    @Json(name = "BlockedUserIDs")
    public val blockedUserIDs: kotlin.collections.List<kotlin.String> = emptyList(),
    @Json(name = "BlockedUsers")
    public val blockedUsers:
        kotlin.collections.List<io.getstream.feeds.android.network.models.User> =
        emptyList(),
    @Json(name = "Egresses")
    public val egresses:
        kotlin.collections.List<io.getstream.feeds.android.network.models.CallEgress> =
        emptyList(),
    @Json(name = "Members")
    public val members:
        kotlin.collections.List<io.getstream.feeds.android.network.models.CallMember> =
        emptyList(),
    @Json(name = "Custom")
    public val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),
    @Json(name = "DeletedAt") public val deletedAt: java.util.Date? = null,
    @Json(name = "EgressUpdatedAt") public val egressUpdatedAt: java.util.Date? = null,
    @Json(name = "EndedAt") public val endedAt: java.util.Date? = null,
    @Json(name = "JoinAheadTimeSeconds") public val joinAheadTimeSeconds: kotlin.Int? = null,
    @Json(name = "LastHeartbeatAt") public val lastHeartbeatAt: java.util.Date? = null,
    @Json(name = "MemberCount") public val memberCount: kotlin.Int? = null,
    @Json(name = "StartsAt") public val startsAt: java.util.Date? = null,
    @Json(name = "CallType")
    public val callType: io.getstream.feeds.android.network.models.CallType? = null,
    @Json(name = "CreatedBy")
    public val createdBy: io.getstream.feeds.android.network.models.User? = null,
    @Json(name = "MemberLookup")
    public val memberLookup: io.getstream.feeds.android.network.models.MemberLookup? = null,
    @Json(name = "Session")
    public val session: io.getstream.feeds.android.network.models.CallSession? = null,
    @Json(name = "Settings")
    public val settings: io.getstream.feeds.android.network.models.CallSettings? = null,
    @Json(name = "SettingsOverrides")
    public val settingsOverrides: io.getstream.feeds.android.network.models.CallSettings? = null,
)
