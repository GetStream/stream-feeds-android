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

data class STTEgressConfig (
    @Json(name = "closed_captions_enabled")
    val closedCaptionsEnabled: kotlin.Boolean? = null,

    @Json(name = "language")
    val language: kotlin.String? = null,

    @Json(name = "storage_name")
    val storageName: kotlin.String? = null,

    @Json(name = "translations_enabled")
    val translationsEnabled: kotlin.Boolean? = null,

    @Json(name = "upload_transcriptions")
    val uploadTranscriptions: kotlin.Boolean? = null,

    @Json(name = "whisper_server_base_url")
    val whisperServerBaseUrl: kotlin.String? = null,

    @Json(name = "translation_languages")
    val translationLanguages: kotlin.collections.List<kotlin.String>? = emptyList(),

    @Json(name = "external_storage")
    val externalStorage: io.getstream.feeds.android.core.generated.models.ExternalStorage? = null
)
