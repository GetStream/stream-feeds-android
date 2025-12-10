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

package io.getstream.feeds.android.client.internal.file

import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadContext
import io.getstream.feeds.android.client.api.file.UploadPayload
import io.getstream.feeds.android.client.api.file.Uploader
import io.getstream.feeds.android.network.models.Attachment
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal suspend fun <C : UploadContext> Uploader<C>.uploadAll(
    files: List<UploadPayload<C>>,
    attachmentUploadProgress: ((UploadPayload<C>, Double) -> Unit)?,
): List<Attachment> {
    if (files.isEmpty()) return emptyList()

    return coroutineScope {
        files
            .map { localFile ->
                async {
                    val progressCallback: ((Double) -> Unit)? =
                        attachmentUploadProgress?.let { progress -> { progress(localFile, it) } }
                    val uploadedFile =
                        upload(payload = localFile, progress = progressCallback).getOrThrow()
                    Attachment(
                        assetUrl = uploadedFile.fileUrl,
                        imageUrl = uploadedFile.fileUrl.takeIf { localFile.type == FileType.Image },
                        thumbUrl = uploadedFile.thumbnailUrl,
                        type = localFile.type.toAttachmentType(),
                    )
                }
            }
            .awaitAll()
    }
}
