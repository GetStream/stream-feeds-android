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
public data class EgressTaskConfig(
    @Json(name = "egress_user")
    public val egressUser: io.getstream.feeds.android.network.models.EgressUser? = null,
    @Json(name = "frame_recording_egress_config")
    public val frameRecordingEgressConfig:
        io.getstream.feeds.android.network.models.FrameRecordingEgressConfig? =
        null,
    @Json(name = "hls_egress_config")
    public val hlsEgressConfig: io.getstream.feeds.android.network.models.HLSEgressConfig? = null,
    @Json(name = "recording_egress_config")
    public val recordingEgressConfig:
        io.getstream.feeds.android.network.models.RecordingEgressConfig? =
        null,
    @Json(name = "rtmp_egress_config")
    public val rtmpEgressConfig: io.getstream.feeds.android.network.models.RTMPEgressConfig? = null,
    @Json(name = "stt_egress_config")
    public val sttEgressConfig: io.getstream.feeds.android.network.models.STTEgressConfig? = null,
)
