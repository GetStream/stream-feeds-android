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
import kotlin.io.*

/**  */
data class CallSettings(
    @Json(name = "audio")
    val audio: io.getstream.feeds.android.core.generated.models.AudioSettings? = null,
    @Json(name = "backstage")
    val backstage: io.getstream.feeds.android.core.generated.models.BackstageSettings? = null,
    @Json(name = "broadcasting")
    val broadcasting: io.getstream.feeds.android.core.generated.models.BroadcastSettings? = null,
    @Json(name = "frame_recording")
    val frameRecording: io.getstream.feeds.android.core.generated.models.FrameRecordSettings? =
        null,
    @Json(name = "geofencing")
    val geofencing: io.getstream.feeds.android.core.generated.models.GeofenceSettings? = null,
    @Json(name = "ingress")
    val ingress: io.getstream.feeds.android.core.generated.models.IngressSettings? = null,
    @Json(name = "limits")
    val limits: io.getstream.feeds.android.core.generated.models.LimitsSettings? = null,
    @Json(name = "recording")
    val recording: io.getstream.feeds.android.core.generated.models.RecordSettings? = null,
    @Json(name = "ring")
    val ring: io.getstream.feeds.android.core.generated.models.RingSettings? = null,
    @Json(name = "screensharing")
    val screensharing: io.getstream.feeds.android.core.generated.models.ScreensharingSettings? =
        null,
    @Json(name = "session")
    val session: io.getstream.feeds.android.core.generated.models.SessionSettings? = null,
    @Json(name = "thumbnails")
    val thumbnails: io.getstream.feeds.android.core.generated.models.ThumbnailsSettings? = null,
    @Json(name = "transcription")
    val transcription: io.getstream.feeds.android.core.generated.models.TranscriptionSettings? =
        null,
    @Json(name = "video")
    val video: io.getstream.feeds.android.core.generated.models.VideoSettings? = null,
)
