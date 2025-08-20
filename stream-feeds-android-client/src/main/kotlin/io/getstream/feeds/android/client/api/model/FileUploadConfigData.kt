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

import io.getstream.feeds.android.core.generated.models.FileUploadConfig

/**
 * Data class representing file upload configuration.
 *
 * @property allowedFileExtensions List of allowed file extensions.
 * @property allowedMimeTypes List of allowed MIME types.
 * @property blockedFileExtensions List of blocked file extensions.
 * @property blockedMimeTypes List of blocked MIME types.
 * @property sizeLimit Maximum allowed file size in bytes.
 */
public data class FileUploadConfigData(
    val allowedFileExtensions: List<String>,
    val allowedMimeTypes: List<String>,
    val blockedFileExtensions: List<String>,
    val blockedMimeTypes: List<String>,
    val sizeLimit: Int,
)

/** Converts [FileUploadConfig] to [FileUploadConfigData]. */
internal fun FileUploadConfig.toModel(): FileUploadConfigData =
    FileUploadConfigData(
        allowedFileExtensions = allowedFileExtensions,
        allowedMimeTypes = allowedMimeTypes,
        blockedFileExtensions = blockedFileExtensions,
        blockedMimeTypes = blockedMimeTypes,
        sizeLimit = sizeLimit,
    )
