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

/** A type representing the app settings. */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class AppResponseFields(
    @Json(name = "async_url_enrich_enabled") public val asyncUrlEnrichEnabled: Boolean,
    @Json(name = "auto_translation_enabled") public val autoTranslationEnabled: Boolean,
    @Json(name = "id") public val id: Int,
    @Json(name = "name") public val name: String,
    @Json(name = "placement") public val placement: String,
    @Json(name = "file_upload_config")
    public val fileUploadConfig: io.getstream.feeds.android.network.models.FileUploadConfig,
    @Json(name = "image_upload_config")
    public val imageUploadConfig: io.getstream.feeds.android.network.models.FileUploadConfig,
)
