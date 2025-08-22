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
package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.network.models.AppResponseFields

/**
 * Data class representing application data configuration.
 *
 * @property asyncUrlEnrichEnabled Whether async URL enrichment is enabled.
 * @property autoTranslationEnabled Whether auto translation is enabled.
 * @property fileUploadConfig Configuration for file uploads.
 * @property imageUploadConfig Configuration for image uploads.
 * @property name Name of the application.
 */
public data class AppData(
    val asyncUrlEnrichEnabled: Boolean,
    val autoTranslationEnabled: Boolean,
    val fileUploadConfig: FileUploadConfigData,
    val imageUploadConfig: FileUploadConfigData,
    val name: String,
)

/** Converts [AppResponseFields] to [AppData]. */
internal fun AppResponseFields.toModel(): AppData =
    AppData(
        asyncUrlEnrichEnabled = asyncUrlEnrichEnabled,
        autoTranslationEnabled = autoTranslationEnabled,
        fileUploadConfig = fileUploadConfig.toModel(),
        imageUploadConfig = imageUploadConfig.toModel(),
        name = name,
    )
