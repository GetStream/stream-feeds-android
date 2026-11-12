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

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class CallActionOptions(
    @Json(name = "duration") public val duration: Int? = null,
    @Json(name = "flag_reason") public val flagReason: String? = null,
    @Json(name = "kick_reason") public val kickReason: String? = null,
    @Json(name = "mute_audio") public val muteAudio: Boolean? = null,
    @Json(name = "mute_video") public val muteVideo: Boolean? = null,
    @Json(name = "reason") public val reason: String? = null,
    @Json(name = "warning_text") public val warningText: String? = null,
)
