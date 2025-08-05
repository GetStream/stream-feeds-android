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

data class Call (
    @Json(name = "AppPK")
    val appPK: kotlin.Int,

    @Json(name = "Backstage")
    val backstage: kotlin.Boolean,

    @Json(name = "CID")
    val cID: kotlin.String,

    @Json(name = "ChannelCID")
    val channelCID: kotlin.String,

    @Json(name = "CreatedAt")
    val createdAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "CreatedByUserID")
    val createdByUserID: kotlin.String,

    @Json(name = "CurrentSessionID")
    val currentSessionID: kotlin.String,

    @Json(name = "ID")
    val iD: kotlin.String,

    @Json(name = "LastSessionID")
    val lastSessionID: kotlin.String,

    @Json(name = "Team")
    val team: kotlin.String,

    @Json(name = "ThumbnailURL")
    val thumbnailURL: kotlin.String,

    @Json(name = "Type")
    val type: kotlin.String,

    @Json(name = "UpdatedAt")
    val updatedAt: org.threeten.bp.OffsetDateTime,

    @Json(name = "BlockedUserIDs")
    val blockedUserIDs: kotlin.collections.List<kotlin.String> = emptyList(),

    @Json(name = "BlockedUsers")
    val blockedUsers: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.User> = emptyList(),

    @Json(name = "Egresses")
    val egresses: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.CallEgress> = emptyList(),

    @Json(name = "Members")
    val members: kotlin.collections.List<io.getstream.feeds.android.core.generated.models.CallMember> = emptyList(),

    @Json(name = "Custom")
    val custom: kotlin.collections.Map<kotlin.String, Any?> = emptyMap(),

    @Json(name = "DeletedAt")
    val deletedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "EgressUpdatedAt")
    val egressUpdatedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "EndedAt")
    val endedAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "JoinAheadTimeSeconds")
    val joinAheadTimeSeconds: kotlin.Int? = null,

    @Json(name = "LastHeartbeatAt")
    val lastHeartbeatAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "MemberCount")
    val memberCount: kotlin.Int? = null,

    @Json(name = "StartsAt")
    val startsAt: org.threeten.bp.OffsetDateTime? = null,

    @Json(name = "CallType")
    val callType: io.getstream.feeds.android.core.generated.models.CallType? = null,

    @Json(name = "CreatedBy")
    val createdBy: io.getstream.feeds.android.core.generated.models.User? = null,

    @Json(name = "MemberLookup")
    val memberLookup: io.getstream.feeds.android.core.generated.models.MemberLookup? = null,

    @Json(name = "Session")
    val session: io.getstream.feeds.android.core.generated.models.CallSession? = null,

    @Json(name = "Settings")
    val settings: io.getstream.feeds.android.core.generated.models.CallSettings? = null,

    @Json(name = "SettingsOverrides")
    val settingsOverrides: io.getstream.feeds.android.core.generated.models.CallSettings? = null
)
