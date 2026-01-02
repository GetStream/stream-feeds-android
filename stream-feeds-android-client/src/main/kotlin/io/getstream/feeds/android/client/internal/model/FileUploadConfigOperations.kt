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

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.FileUploadConfigData
import io.getstream.feeds.android.network.models.FileUploadConfig

/**
 * Converts [io.getstream.feeds.android.network.models.FileUploadConfig] to
 * [io.getstream.feeds.android.client.api.model.FileUploadConfigData].
 */
internal fun FileUploadConfig.toModel(): FileUploadConfigData =
    FileUploadConfigData(
        allowedFileExtensions = allowedFileExtensions,
        allowedMimeTypes = allowedMimeTypes,
        blockedFileExtensions = blockedFileExtensions,
        blockedMimeTypes = blockedMimeTypes,
        sizeLimit = sizeLimit,
    )
