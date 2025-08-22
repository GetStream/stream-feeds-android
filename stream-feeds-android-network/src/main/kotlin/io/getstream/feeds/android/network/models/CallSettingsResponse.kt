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
public data class CallSettingsResponse(
    @Json(name = "audio")
    public val audio: io.getstream.feeds.android.network.models.AudioSettingsResponse,
    @Json(name = "backstage")
    public val backstage: io.getstream.feeds.android.network.models.BackstageSettingsResponse,
    @Json(name = "broadcasting")
    public val broadcasting: io.getstream.feeds.android.network.models.BroadcastSettingsResponse,
    @Json(name = "frame_recording")
    public val frameRecording:
        io.getstream.feeds.android.network.models.FrameRecordingSettingsResponse,
    @Json(name = "geofencing")
    public val geofencing: io.getstream.feeds.android.network.models.GeofenceSettingsResponse,
    @Json(name = "limits")
    public val limits: io.getstream.feeds.android.network.models.LimitsSettingsResponse,
    @Json(name = "recording")
    public val recording: io.getstream.feeds.android.network.models.RecordSettingsResponse,
    @Json(name = "ring")
    public val ring: io.getstream.feeds.android.network.models.RingSettingsResponse,
    @Json(name = "screensharing")
    public val screensharing:
        io.getstream.feeds.android.network.models.ScreensharingSettingsResponse,
    @Json(name = "session")
    public val session: io.getstream.feeds.android.network.models.SessionSettingsResponse,
    @Json(name = "thumbnails")
    public val thumbnails: io.getstream.feeds.android.network.models.ThumbnailsSettingsResponse,
    @Json(name = "transcription")
    public val transcription:
        io.getstream.feeds.android.network.models.TranscriptionSettingsResponse,
    @Json(name = "video")
    public val video: io.getstream.feeds.android.network.models.VideoSettingsResponse,
    @Json(name = "ingress")
    public val ingress: io.getstream.feeds.android.network.models.IngressSettingsResponse? = null,
)
