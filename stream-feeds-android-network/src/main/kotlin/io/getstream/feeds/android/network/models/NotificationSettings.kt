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
public data class NotificationSettings(
    @Json(name = "enabled") public val enabled: kotlin.Boolean,
    @Json(name = "call_live_started")
    public val callLiveStarted: io.getstream.feeds.android.network.models.EventNotificationSettings,
    @Json(name = "call_missed")
    public val callMissed: io.getstream.feeds.android.network.models.EventNotificationSettings,
    @Json(name = "call_notification")
    public val callNotification:
        io.getstream.feeds.android.network.models.EventNotificationSettings,
    @Json(name = "call_ring")
    public val callRing: io.getstream.feeds.android.network.models.EventNotificationSettings,
    @Json(name = "session_started")
    public val sessionStarted: io.getstream.feeds.android.network.models.EventNotificationSettings,
)
